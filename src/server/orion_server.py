import aiomysql
import os
from quart import Quart, request, send_file, send_from_directory, jsonify, abort
from PIL import Image
from io import BytesIO
import time
from datetime import datetime

MAX_QUERY_LIMIT = 1000

from config import DATABASE_CONFIG, SERVER_CONFIG, IMAGE_FOLDER, DEFAULT_IMAGES, POST_CONFIG

app = Quart(__name__)

#region ####################################### GET REQUESTS #######################################
#region #### UITLITY FUNCTIONS ####

# Async function to get the database connection
async def get_db_connection():
    conn = await aiomysql.connect(
        host=DATABASE_CONFIG['host'],
        port=DATABASE_CONFIG['port'],
        user=DATABASE_CONFIG['user'],
        password=DATABASE_CONFIG['password'],
        db=DATABASE_CONFIG['db'],
        autocommit=True  # Ensure changes are committed without having to call conn.commit() explicitly
    )
    return conn

def validate_and_construct_query(initial_query, params_to_validate):
    query = initial_query
    params = []

    for param in params_to_validate:
        name, value, expected_type = param['name'], param['value'], param['type']
        
        if value is not None:
            if expected_type == 'int':
                try:
                    value = int(value)
                    query += f" AND {name} = %s"
                    params.append(value)
                except ValueError:
                    abort(400, description=f"Invalid {name} parameter")
            elif expected_type == 'string':
                query += f" AND {name} LIKE %s"
                params.append(f"%{value}%")
            # Add more types as needed

    return query, params

async def execute_query(query, params):
    async with await get_db_connection() as conn:
        async with conn.cursor(aiomysql.DictCursor) as cursor:
            await cursor.execute(query, params)
            result = await cursor.fetchall()
            return jsonify(result)

#endregion

#region #### ROUTES ####

#region ## DATABASE QUERIES ##
#assets route
@app.route('/assets', methods=['GET'])
async def get_assets():
    asset_id = request.args.get('asset_id', default=None)
    asset_name = request.args.get('asset_name', default=None)
    type_id = request.args.get('type_id', default=None)
    #date_added = request.args.get('date_added', default=None)
    #date_last_maintained = request.args.get('date_last_maintained', default=None)
    is_active = request.args.get('is_active', default=None)

    initial_query = ("SELECT "+
                     "asset_id, "+
                     "asset_name, "+
                     "type_id, "+
                     "asset_desc, "+
                     "UNIX_TIMESTAMP(date_added) as date_added, "+
                     "UNIX_TIMESTAMP(date_last_serviced) as date_last_serviced, "+
                     "is_active "+
                     "FROM assets WHERE 1=1")
    params_to_validate = [
        {'name': 'asset_id', 'value': asset_id, 'type': 'int'},
        {'name': 'asset_name', 'value': asset_name, 'type': 'string'},
        {'name': 'type_id', 'value': type_id, 'type': 'int'},
        #{'name': 'date_added', 'value': date_added, 'type': 'int'},
        #{'name': 'date_last_maintained', 'value': date_last_maintained, 'type': 'int'},
        {'name': 'is_active', 'value': is_active, 'type': 'int'},
    ]
    
    query, params = validate_and_construct_query(initial_query, params_to_validate)
    query += f" ORDER BY asset_id LIMIT {MAX_QUERY_LIMIT}"

    return await execute_query(query, params)

# asset_types route
@app.route('/asset_types', methods=['GET'])
async def get_asset_types():
    type_id = request.args.get('type_id', default=None)
    type_desc = request.args.get('type_desc', default=None)

    initial_query = "SELECT type_id, type_desc FROM asset_types WHERE 1=1"
    params_to_validate = [
        {'name': 'type_id', 'value': type_id, 'type': 'int'},
        {'name': 'type_desc', 'value': type_desc, 'type': 'string'},
    ]
    
    query, params = validate_and_construct_query(initial_query, params_to_validate)
    query += f" ORDER BY type_id LIMIT {MAX_QUERY_LIMIT}"

    return await execute_query(query, params)

# checklists route
@app.route('/checklists', methods=['GET'])
async def get_checklists():
    checklist_id = request.args.get('checklist_id', default=None)
    asset_id = request.args.get('asset_id', default=None)

    initial_query = "SELECT * FROM checklists WHERE 1=1"
    params_to_validate = [
        {'name': 'checklist_id', 'value': checklist_id, 'type': 'int'},
        {'name': 'asset_id', 'value': asset_id, 'type': 'int'},
    ]
    
    query, params = validate_and_construct_query(initial_query, params_to_validate)
    query += f" ORDER BY checklist_id LIMIT {MAX_QUERY_LIMIT}"

    return await execute_query(query, params)

# maintenance records route
@app.route('/records', methods=['GET'])
async def get_records():
    service_id = request.args.get('service_id', default=None)
    asset_id = request.args.get('asset_id', default=None)
    user_id = request.args.get('user_id', default=None)

    initial_query = ("SELECT "+
                    "service_id, "+
                    "asset_id, "+
                    "user_id, "+
                    "service_desc, "+
                    "UNIX_TIMESTAMP(date_recorded) AS date_recorded, "+
                    "UNIX_TIMESTAMP(date_serviced) AS date_serviced "+
                    "FROM records WHERE 1=1 ")
    params_to_validate = [
        {'name': 'service_id', 'value': service_id, 'type': 'int'},
        {'name': 'asset_id', 'value': asset_id, 'type': 'int'},
        {'name': 'user_id', 'value': user_id, 'type': 'int'},
    ]
    
    query, params = validate_and_construct_query(initial_query, params_to_validate)
    query += f" ORDER BY service_id LIMIT {MAX_QUERY_LIMIT}"

    return await execute_query(query, params)

# users info route
@app.route('/users', methods=['GET'])
async def get_users():
    user_id = request.args.get('user_id', default=None)
    user_name = request.args.get('user_name', default=None)

    initial_query = "SELECT * FROM users WHERE 1=1"
    params_to_validate = [
        {'name': 'user_id', 'value': user_id, 'type': 'int'},
        {'name': 'user_name', 'value': user_name, 'type': 'string'},
    ]
    
    query, params = validate_and_construct_query(initial_query, params_to_validate)
    query += f" ORDER BY user_id LIMIT {MAX_QUERY_LIMIT}"

    return await execute_query(query, params)

#endregion

#region ## IMAGE GET ROUTES ##

@app.route('/img/<category>/<filename>', methods=['GET'])
async def serve_image(category, filename):
    # Map URL category to folder and fallback image
    category_map = {
        'asset': 'asset_images',
        'service': 'service_images',
        'user': 'user_images',
    }

    if category not in category_map:
        abort(404, "Category not found")

    image_folder = IMAGE_FOLDER[category_map[category]]
    fallback_image = DEFAULT_IMAGES[category_map[category]]

    # Prevent path traversal attacks
    if '..' in filename or filename.startswith('/'):
        abort(400, "Invalid filename")

    image_path = os.path.join(image_folder, filename)

    if not os.path.isfile(image_path):
        # If the requested image does not exist, serve the fallback image
        if os.path.isfile(fallback_image):
            return await send_file(fallback_image)
        else:
            abort(404, "Image not found and fallback image is missing")

    return await send_from_directory(image_folder, filename)
#endregion
#endregion
#endregion

#region ####################################### POST REQUESTS #######################################
#region #### UTILITY FUNCTIONS ####
MAX_AVATAR_IMAGE_FILE_SIZE = 100 * 1024 * 1024  # 100 MB
MAX_ASSET_IMAGE_FILE_SIZE = 500 * 1024 * 1024  # 500 MB
ALLOWED_EXTENSIONS = {'png', 'jpg', 'jpeg'}

def allowed_file(filename):
    return '.' in filename and filename.rsplit('.', 1)[1].lower() in ALLOWED_EXTENSIONS

def resize_image(image, size, mode):
    return image.resize(size, mode, reducing_gap=3.0)

# Convert datetime strings to Unix timestamps if necessary
def to_unix_timestamp(date_str_or_unix):
    try:
        # First, attempt to convert to int, assuming it might be a valid Unix timestamp
        unix_timestamp = int(date_str_or_unix)
        if unix_timestamp > 0:
            return unix_timestamp
    except ValueError:
        # If conversion to int fails, it's not a valid Unix timestamp
        pass
    
    # If not a valid Unix timestamp, try converting from datetime string
    date_str_or_unix = str(date_str_or_unix).strip()  # Ensure it's a string and strip any leading/trailing whitespace
    dt_format = '%Y%m%d%H%M%S'
    try:
        return int(datetime.strptime(date_str_or_unix, dt_format).timestamp())
    except ValueError:
        # If conversion fails, return the original value to handle the error elsewhere
        return date_str_or_unix

#endregion

#region #### ROUTES ####

#region ## DATABASE INSERT ROUTES ##
@app.route('/assets', methods=['POST'])
async def add_asset():
    data = await request.json
    if not data:
        abort(400, description='Missing json request body')

    # Extract data from the request
    asset_id = data.get('asset_id', None)
    asset_name = data.get('asset_name')
    type_id = data.get('type_id')
    asset_desc = data.get('asset_desc')
    date_added = data.get('date_added')
    date_last_serviced = data.get('date_last_serviced', None)
    is_active = data.get('is_active')

    # Check for missing required fields
    if not asset_name or not type_id or not asset_desc or not date_added or 'is_active' not in data:
        missing_fields_message = 'Missing required fields: '
        missing_fields = [field for field in ['asset_name', 'type_id', 'asset_desc', 'date_added', 'is_active'] if not data.get(field)]
        abort(400, description=missing_fields_message + ', '.join(missing_fields))
    
    async with await get_db_connection() as conn:
        async with conn.cursor() as cursor:
            if asset_id is not None:
                # Check if the provided asset_id already exists
                await cursor.execute("SELECT 1 FROM assets WHERE asset_id = %s", (asset_id,))
                if await cursor.fetchone():
                    # If asset_id exists, update the existing record
                    query = "UPDATE assets SET asset_name=%s, type_id=%s, asset_desc=%s, date_added=FROM_UNIXTIME(%s), date_last_serviced=FROM_UNIXTIME(%s), is_active=%s WHERE asset_id=%s"
                    params = (asset_name, type_id, asset_desc, date_added, date_last_serviced, is_active, asset_id)
                else:
                    # If asset_id does not exist, insert a new record with the provided asset_id
                    query = "INSERT INTO assets (asset_id, asset_name, type_id, asset_desc, date_added, date_last_serviced, is_active) VALUES (%s, %s, %s, %s, FROM_UNIXTIME(%s), FROM_UNIXTIME(%s), %s)"
                    params = (asset_id, asset_name, type_id, asset_desc, date_added, date_last_serviced, is_active)
            else:
                # If no asset_id is provided, insert a new record without specifying asset_id to utilize AUTOINCREMENT
                query = "INSERT INTO assets (asset_name, type_id, asset_desc, date_added, date_last_serviced, is_active) VALUES (%s, %s, %s, FROM_UNIXTIME(%s), FROM_UNIXTIME(%s), %s)"
                params = (asset_name, type_id, asset_desc, date_added, date_last_serviced, is_active)

            await cursor.execute(query, params)
            if asset_id is None:
                # If asset_id was not provided, fetch the auto-generated ID
                asset_id = cursor.lastrowid

    return jsonify({'message': 'Asset processed successfully', 'asset_id': asset_id}), 201



# Add a new asset type (Does not support updating existing types)
@app.route('/asset_types', methods=['POST'])
async def add_asset_type():
    data = await request.json
    if not data or 'type_desc' not in data:
        abort(400, description='Missing required field: type_desc')

    type_desc = data['type_desc']
    type_id = data.get('type_id', None)

    async with await get_db_connection() as conn:
        async with conn.cursor() as cursor:
            if type_id is not None:
                # Check if the type_id is already taken
                await cursor.execute("SELECT 1 FROM asset_types WHERE type_id = %s", (type_id,))
                if await cursor.fetchone():
                    # If type_id is taken, treat it as if type_id was not included in the request
                    type_id = None
            
            if type_id is None:
                # Insert without specifying type_id to rely on auto-increment
                await cursor.execute("INSERT INTO asset_types (type_desc) VALUES (%s)", (type_desc,))
            else:
                # Insert with specified type_id
                await cursor.execute("INSERT INTO asset_types (type_id, type_desc) VALUES (%s, %s)", (type_id, type_desc))

            # Get the ID of the newly inserted row
            type_id = cursor.lastrowid

            await conn.commit()  # Ensure changes are committed, in case autocommit is not enabled

    return jsonify({'message': 'Asset Type added successfully', 'type_id': type_id}), 201


# Add a new checklist (Updates existing checklists if checklist_id is provided and exists)
@app.route('/checklists', methods=['POST'])
async def add_checklist():
    data = await request.json
    if not data:
        abort(400, description='Missing json request body')

    checklist_id = data.get('checklist_id', None)
    asset_id = data.get('asset_id')
    checklist_title = data.get('checklist_title')
    checklist_desc = data.get('checklist_desc')

    if not asset_id or not checklist_title or not checklist_desc:
        missing_fields_message = 'Missing required fields: '
        missing_fields = [field for field in ['asset_id', 'checklist_title', 'checklist_desc'] if not data.get(field)]
        abort(400, description=missing_fields_message + ', '.join(missing_fields))

    async with await get_db_connection() as conn:
        async with conn.cursor() as cursor:
            if checklist_id is not None:
                # Check if the provided checklist_id already exists
                await cursor.execute("SELECT 1 FROM checklists WHERE checklist_id = %s", (checklist_id,))
                if await cursor.fetchone():
                    # If checklist_id exists, update the existing record
                    query = "UPDATE checklists SET asset_id=%s, checklist_title=%s, checklist_desc=%s WHERE checklist_id=%s"
                    params = (asset_id, checklist_title, checklist_desc, checklist_id)
                else:
                    # If checklist_id does not exist, insert a new record with the provided checklist_id
                    query = "INSERT INTO checklists (checklist_id, asset_id, checklist_title, checklist_desc) VALUES (%s, %s, %s, %s)"
                    params = (checklist_id, asset_id, checklist_title, checklist_desc)
            else:
                # If no checklist_id is provided, insert a new record without specifying checklist_id to utilize AUTOINCREMENT
                query = "INSERT INTO checklists (asset_id, checklist_title, checklist_desc) VALUES (%s, %s, %s)"
                params = (asset_id, checklist_title, checklist_desc)

            await cursor.execute(query, params)
            if checklist_id is None:
                # If checklist_id was not provided, fetch the auto-generated ID
                checklist_id = cursor.lastrowid

    return jsonify({'message': 'Checklist processed successfully', 'checklist_id': checklist_id}), 201


# Add a new maintenance record (Updates existing records if service_id is provided and exists)
@app.route('/records', methods=['POST'])
async def add_record():
    data = await request.json
    if not data:
        abort(400, description='Missing json request body')

    service_id = data.get('service_id', None)
    asset_id = data.get('asset_id')
    user_id = data.get('user_id')
    service_desc = data.get('service_desc')
    date_recorded = data.get('date_recorded')
    date_serviced = data.get('date_serviced', None)

    if not asset_id or not user_id or not service_desc or not date_recorded:
        missing_fields_message = 'Missing required fields: '
        missing_fields = [field for field in ['asset_id', 'user_id', 'service_desc', 'date_recorded'] if not data.get(field)]
        abort(400, description=missing_fields_message + ', '.join(missing_fields))

    async with await get_db_connection() as conn:
        async with conn.cursor() as cursor:
            if service_id is not None:
                # Check if the provided service_id already exists
                await cursor.execute("SELECT 1 FROM records WHERE service_id = %s", (service_id,))
                if await cursor.fetchone():
                    # If service_id exists, update the existing record
                    query = "UPDATE records SET asset_id=%s, user_id=%s, service_desc=%s, date_recorded=FROM_UNIXTIME(%s), date_serviced=FROM_UNIXTIME(%s) WHERE service_id=%s"
                    params = (asset_id, user_id, service_desc, date_recorded, date_serviced, service_id)
                else:
                    # If service_id does not exist, insert a new record with the provided service_id
                    query = "INSERT INTO records (service_id, asset_id, user_id, service_desc, date_recorded, date_serviced) VALUES (%s, %s, %s, %s, FROM_UNIXTIME(%s), FROM_UNIXTIME(%s))"
                    params = (service_id, asset_id, user_id, service_desc, date_recorded, date_serviced)
            else:
                # If no service_id is provided, insert a new record without specifying service_id to utilize AUTOINCREMENT
                query = "INSERT INTO records (asset_id, user_id, service_desc, date_recorded, date_serviced) VALUES (%s, %s, %s, FROM_UNIXTIME(%s), FROM_UNIXTIME(%s))"
                params = (asset_id, user_id, service_desc, date_recorded, date_serviced)

            await cursor.execute(query, params)
            if service_id is None:
                # If service_id was not provided, fetch the auto-generated ID
                service_id = cursor.lastrowid

    return jsonify({'message': 'Record processed successfully', 'service_id': service_id}), 201



# Add a new user (Does not support updating existing users)
@app.route('/users', methods=['POST'])
async def add_user():
    data = await request.json
    if not data:
        abort(400, description='Missing json request body')

    user_name = data.get('user_name')
    user_id = data.get('user_id', None)  # Optional user_id from request

    if not user_name:
        abort(400, description='Missing user_name field')

    async with await get_db_connection() as conn:
        async with conn.cursor() as cursor:
            # Check if the user_id is provided and already exists
            if user_id is not None:
                await cursor.execute("SELECT 1 FROM users WHERE user_id = %s", (user_id,))
                if await cursor.fetchone():
                    user_id = None  # If user_id exists, fallback to auto-increment
            
            if user_id is None:
                # If no user_id provided or it's already taken, rely on auto-increment
                await cursor.execute(
                    "INSERT INTO users (user_name) VALUES (%s)",
                    (user_name,)
                )
            else:
                # If a user_id is provided and it's not taken, use it
                await cursor.execute(
                    "INSERT INTO users (user_id, user_name) VALUES (%s, %s)",
                    (user_id, user_name)
                )

            user_id = cursor.lastrowid  # Fetch the ID of the newly inserted/updated row
            await conn.commit()  # Commit the transaction, in case autocommit is not enabled

    return jsonify({'message': 'User added successfully', 'user_id': user_id}), 201

#endregion

#region ## IMAGE UPLOAD ROUTES ##

# For storing user avatars
@app.route('/uploads/img/user', methods=['POST'])
async def upload_user_avatar():
    user_id = request.headers.get(POST_CONFIG['user_image_id_header'])
    if not user_id or not user_id.isdigit():
        return jsonify({'error': 'Invalid or missing user_id header'}), 400
    
    if POST_CONFIG['form_user_image_key'] not in await request.files:
        abort(400, description='No file part in the request')
    
    file = (await request.files)[POST_CONFIG['form_user_image_key']]
    if file.filename == '':
        abort(400, description='No file selected for uploading')
    
    if file and allowed_file(file.filename):
        if file.content_length > MAX_AVATAR_IMAGE_FILE_SIZE:
            abort(400, description='File is too large')
        
        try:
            image = Image.open(file.stream)
            image = resize_image(image, (1024, 1024), Image.LANCZOS)
            if image.mode in ("RGBA", "P"):
                image = image.convert("RGBA")
            else:
                image = image.convert("RGB")
            
            filename = f"{user_id}.png"
            image_path = os.path.join(IMAGE_FOLDER['user_images'], filename)
            image.save(image_path, format='PNG')
            
            return jsonify({'message': 'Avatar Uploaded Successfully', 'filename': filename}), 200
        except Exception as e:
            abort(400, description=str(e))
    else:
        abort(400, description='Allowed file types are png, jpg, jpeg')

# For storing asset preview images which will appear in the asset's detail page, and in the asset's list page
@app.route('/uploads/img/asset', methods=['POST'])
async def upload_asset_image():
    asset_id = request.headers.get(POST_CONFIG['asset_image_id_header'])
    if not asset_id or not asset_id.isdigit():
        return jsonify({'error': 'Invalid or missing asset_id header'}), 400
    
    if POST_CONFIG['form_asset_image_key'] not in await request.files:
        abort(400, description='No file part in the request')
    
    file = (await request.files)[POST_CONFIG['form_asset_image_key']]
    if file.filename == '':
        abort(400, description='No file selected for uploading')
    
    if file and allowed_file(file.filename):
        if file.content_length > MAX_ASSET_IMAGE_FILE_SIZE:
            abort(400, description='File is too large')
        
        try:
            image = Image.open(file.stream)
            image = resize_image(image, (1920, 1080), Image.LANCZOS)
            if image.mode == "RGBA":
                image = image.convert("RGB")
            
            filename = f"{asset_id}.jpeg"
            image_path = os.path.join(IMAGE_FOLDER['asset_images'], filename)
            image.save(image_path, format='JPEG')
            
            return jsonify({'message': 'Asset Preview Image Uploaded Successfully', 'filename': filename}), 200
        except Exception as e:
            abort(400, description=str(e))
    else:
        abort(400, description='Allowed file types are png, jpg, jpeg')

# For storing images taken as part of the asset's inspection
@app.route('/uploads/img/service', methods=['POST'])
async def upload_service_image():
    maint_id = request.headers.get(POST_CONFIG['service_image_id_header'])
    if not maint_id or not maint_id.isdigit():
        return jsonify({'error': 'Invalid or missing service_id header'}), 400
    
    if POST_CONFIG['form_service_image_key'] not in await request.files:
        abort(400, description='No file part in the request')
    
    file = (await request.files)[POST_CONFIG['form_service_image_key']]
    if file.filename == '':
        abort(400, description='No file selected for uploading')
    
    if file and allowed_file(file.filename):
        if file.content_length > MAX_ASSET_IMAGE_FILE_SIZE:
            abort(400, description='File is too large')
        
        try:
            image = Image.open(file.stream)
            image = resize_image(image, (1920, 1080), Image.LANCZOS)
            if image.mode == "RGBA":
                image = image.convert("RGB")
            
            filename = f"{maint_id}.jpeg"
            image_path = os.path.join(IMAGE_FOLDER['service_images'], filename)
            image.save(image_path, format='JPEG')
            
            return jsonify({'message': 'Service Image Uploaded Successfully', 'filename': filename}), 200
        except Exception as e:
            abort(400, description=str(e))
    else:
        abort(400, description='Allowed file types are png, jpg, jpeg')
#endregion

#endregion
        
#region ####################################### DELETE REQUESTS #######################################
# Delete an asset by asset_id
@app.route('/assets/<int:asset_id>', methods=['DELETE'])
async def delete_asset(asset_id):
    if asset_id is None:
        # If no asset_id is provided in the URL, return a 400 Bad Request
        abort(400, description='Missing asset_id in the request')

    async with await get_db_connection() as conn:
        async with conn.cursor() as cursor:
            # First, check if the asset with the specified asset_id exists
            await cursor.execute("SELECT 1 FROM assets WHERE asset_id = %s", (asset_id,))
            if await cursor.fetchone() is None:
                # If the asset does not exist, return a 404 Not Found
                abort(404, description='Asset not found')

            # If the asset exists, delete it
            await cursor.execute("DELETE FROM assets WHERE asset_id = %s", (asset_id,))
            # Assuming your database setup commits automatically or you handle commit elsewhere

    # Return a 200 OK response indicating successful deletion
    return jsonify({'message': 'Asset deleted successfully'}), 200

# Delete a checklist by checklist_id
@app.route('/checklists/<int:checklist_id>', methods=['DELETE'])
async def delete_checklist(checklist_id):
    if checklist_id is None:
        # If no checklist_id is provided in the URL, return a 400 Bad Request
        abort(400, description='Missing checklist_id in the request')

    async with await get_db_connection() as conn:
        async with conn.cursor() as cursor:
            # First, check if the checklist with the specified checklist_id exists
            await cursor.execute("SELECT 1 FROM checklists WHERE checklist_id = %s", (checklist_id,))
            if await cursor.fetchone() is None:
                # If the checklist does not exist, return a 404 Not Found
                abort(404, description='Checklist not found')

            # If the checklist exists, delete it
            await cursor.execute("DELETE FROM checklists WHERE checklist_id = %s", (checklist_id,))

    # Return a 200 OK response indicating successful deletion
    return jsonify({'message': 'Checklist deleted successfully'}), 200

# Delete a maintenance record by service_id
@app.route('/records/<int:service_id>', methods=['DELETE'])
async def delete_record(service_id):
    if service_id is None:
        # If no service_id is provided in the URL, return a 400 Bad Request
        abort(400, description='Missing service_id in the request')

    async with await get_db_connection() as conn:
        async with conn.cursor() as cursor:
            # First, check if the record with the specified service_id exists
            await cursor.execute("SELECT 1 FROM records WHERE service_id = %s", (service_id,))
            if await cursor.fetchone() is None:
                # If the record does not exist, return a 404 Not Found
                abort(404, description='Record not found')

            # If the record exists, delete it
            await cursor.execute("DELETE FROM records WHERE service_id = %s", (service_id,))

    # Return a 200 OK response indicating successful deletion
    return jsonify({'message': 'Record deleted successfully'}), 200



if __name__ == '__main__':
    app.run(host=SERVER_CONFIG['host'], port=SERVER_CONFIG['port'], debug=True)
