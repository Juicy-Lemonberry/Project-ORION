import os
import aiomysql
from project_orion_server import app
from quart import request, send_file, send_from_directory, jsonify, abort
from config import IMAGE_FOLDER, DEFAULT_IMAGES, DATABASE_CONFIG
import aiomysql


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


# GET Request Section
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
