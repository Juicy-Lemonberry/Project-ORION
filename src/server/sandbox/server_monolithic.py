from quart import Quart, request, send_file, send_from_directory, jsonify, abort
import aiomysql
import os
from config import DATABASE_CONFIG, SERVER_CONFIG, IMAGE_FOLDER, DEFAULT_IMAGES
from PIL import Image

app = Quart(__name__)

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


####################################### GET Request Section #######################################
#assets route
@app.route('/assets', methods=['GET'])
async def get_assets():
    asset_id = request.args.get('asset_id', default=None)
    asset_name = request.args.get('asset_name', default=None)
    asset_type = request.args.get('asset_type', default=None)
    date_added = request.args.get('date_added', default=None)
    date_last_maintained = request.args.get('date_last_maintained', default=None)
    in_service = request.args.get('in_service', default=None)

    initial_query = "SELECT * FROM assets WHERE 1=1"
    params_to_validate = [
        {'name': 'asset_id', 'value': asset_id, 'type': 'int'},
        {'name': 'asset_name', 'value': asset_name, 'type': 'string'},
        {'name': 'asset_type', 'value': asset_type, 'type': 'int'},
        {'name': 'date_added', 'value': date_added, 'type': 'int'},
        {'name': 'date_last_maintained', 'value': date_last_maintained, 'type': 'int'},
        {'name': 'in_service', 'value': in_service, 'type': 'int'},
    ]
    
    query, params = validate_and_construct_query(initial_query, params_to_validate)
    query += " ORDER BY asset_id LIMIT 10"

    return await execute_query(query, params)

# asset_types route
@app.route('/asset_types', methods=['GET'])
async def get_asset_types():
    type_id = request.args.get('type_id', default=None)
    type_desc = request.args.get('type_desc', default=None)

    initial_query = "SELECT * FROM asset_types WHERE 1=1"
    params_to_validate = [
        {'name': 'type_id', 'value': type_id, 'type': 'int'},
        {'name': 'type_desc', 'value': type_desc, 'type': 'string'},
    ]
    
    query, params = validate_and_construct_query(initial_query, params_to_validate)
    query += " ORDER BY type_id LIMIT 10"

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
    query += " ORDER BY checklist_id LIMIT 10"

    return await execute_query(query, params)

# maintenance records route
@app.route('/records', methods=['GET'])
async def get_records():
    maint_id = request.args.get('maint_id', default=None)
    asset_id = request.args.get('asset_id', default=None)

    initial_query = "SELECT * FROM records WHERE 1=1"
    params_to_validate = [
        {'name': 'maint_id', 'value': maint_id, 'type': 'int'},
        {'name': 'asset_id', 'value': asset_id, 'type': 'int'},
    ]
    
    query, params = validate_and_construct_query(initial_query, params_to_validate)
    query += " ORDER BY maint_id LIMIT 10"

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
    query += " ORDER BY user_id LIMIT 10"

    return await execute_query(query, params)

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


####################################### POST Request Section #######################################

#a constant for the max image file size:
MAX_FILE_SIZE = 100 * 1024 * 1024  # 100 MB


# Function to save an image to the specified path
async def save_image(file, save_path, format, size, scale_method):
    # Check file size
    file.seek(0, os.SEEK_END)  # Move cursor to the end of file to get its size
    file_length = file.tell()
    if file_length > MAX_FILE_SIZE:
        return False, "File size exceeds the maximum limit of 100 MB."
    file.seek(0)  # Reset cursor position to the start of the file
    
    # Proceed with the existing image processing and saving logic
    with Image.open(file.stream) as img:
        # Resize and scale based on the specified method
        if scale_method == 'UniformToFill':
            img.thumbnail(size, Image.ANTIALIAS, reducing_gap=3.0)
        elif scale_method == 'Fill':
            img = img.resize(size, Image.ANTIALIAS)
        
        img.save(save_path, format=format)
    return True, "Success"

def sanitize_filename(filename):
    # Remove path separators from the filename to prevent directory traversal
    return os.path.basename(filename)


# Function to validate POST request headers
async def validate_and_extract_header(request, header_name, expected_type):
    header_value = request.headers.get(header_name)
    if not header_value:
        return None, f"{header_name} header is required"
    try:
        if expected_type == 'int':
            return int(header_value), None
    except ValueError:
        return None, f"Invalid {header_name} value"
    if expected_type == 'string':
        return sanitize_filename(header_value), None
    return None, "Unsupported header type"


# POST Request Section

# Route to upload user profile image
@app.route('/upload/img/user/', methods=['POST'])
async def upload_user_image():
    print("Route hit")  # Debugging line
    print("Headers:", request.headers)  # Debugging line
    user_id, error = await validate_and_extract_header(request, "user_id", "int")
    if error:
        return jsonify({'message': error}), 400

    if 'image' not in await request.files:
        return jsonify({'message': 'No image provided'}), 400

    image = (await request.files)['image']
    filename = f"{user_id}.png"
    save_path = os.path.join(IMAGE_FOLDER['user_images'], filename)
    
    success, message = await save_image(image, save_path, format="PNG", size=(1024, 1024), scale_method='UniformToFill')
    if not success:
        return jsonify({'message': message}), 400  # Return error if file size check fails
    
    return jsonify({'message': 'Image uploaded successfully', 'path': save_path})

# Route to upload asset preview images
@app.route('/upload/img/asset', methods=['POST'])
async def upload_asset_image():
    asset_id, error = await validate_and_extract_header(request, "asset_id", "int")
    if error:
        return jsonify({'message': error}), 400

    if 'image' not in await request.files:
        return jsonify({'message': 'No image provided'}), 400

    image = (await request.files)['image']
    filename = f"{asset_id}.jpg"
    save_path = os.path.join(IMAGE_FOLDER['asset_images'], filename)
    
    success, message = await save_image(image, save_path, format="JPEG", size=(1024, 1024), scale_method='UniformToFill')
    if not success:
        return jsonify({'message': message}), 400  # Return error if file size check fails
    
    return jsonify({'message': 'Image uploaded successfully', 'path': save_path})

# Route to upload service images
@app.route('/upload/img/service', methods=['POST'])
async def upload_service_image():
    filename, error = await validate_and_extract_header(request, "filename", "string")
    if error:
        return jsonify({'message': error}), 400
    
    maint_id, error = await validate_and_extract_header(request, "maint_id", "int")
    if error:
        return jsonify({'message': error}), 400

    if 'image' not in await request.files:
        return jsonify({'message': 'No image provided'}), 400

    image = (await request.files)['image']
    filename = f"{maint_id}-{sanitize_filename(filename)}"
    save_path = os.path.join(IMAGE_FOLDER['service_images'], filename)
    
    success, message = await save_image(image, save_path, format="JPEG", size=(1920, 1080), scale_method='Fill')
    if not success:
        return jsonify({'message': message}), 400  # Return error if file size check fails
    
    return jsonify({'message': 'Image uploaded successfully', 'path': save_path})


if __name__ == '__main__':
    app.run(host=SERVER_CONFIG['host'], port=SERVER_CONFIG['port'], debug=True)
