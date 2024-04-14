from project_orion_server import app

import os
from quart import request, jsonify
from config import IMAGE_FOLDER
from PIL import Image


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
