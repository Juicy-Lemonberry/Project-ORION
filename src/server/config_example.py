import os

# Get the directory of the current script
script_dir = os.path.dirname(os.path.abspath(__file__))

#rename this file to config.py before starting the server script.

#Edit the values below to match your database connection details
DATABASE_CONFIG = {
    'host': 'localhost',
    'port': 3306,
    'user': 'your_user',
    'password': 'your_password',
    'db': 'your_database',
}

#These configs tell the server which IP and port to listen on
SERVER_CONFIG = {
    'host': '127.0.0.1',
    'port': 8080
}

#These configs tell the server where to store the images. Change them to match your setup
IMAGE_FOLDER = {
    'asset_images' : "/home/example/project_orion/asset_images",
    'user_images' : "/home/example/project_orion/user_images",
    'service_images' : "/home/example/project_orion/service_images",
}

# Configuration for default images, using script_dir to construct the path
DEFAULT_IMAGES = {
    'asset_images': os.path.join(script_dir, "default_images/asset_image_default.jpg"),
    'user_images': os.path.join(script_dir, "default_images/user_image_default.jpg"),
    'service_images': os.path.join(script_dir, "default_images/service_image_default.jpg"),
}

# Configuration Settings for reading POST requests
POST_CONFIG = {
    'allowed_image_extensions': {'png', 'jpg', 'jpeg'},
    'max_avatar_image_size': 1024 * 1024 * 100, # 100MB
    'max_asset_image_size': 1024 * 1024 * 500, # 500MB,
    'user_image_id_header': 'user_id',
    'asset_image_id_header': 'asset_id',
    'service_image_id_header': 'service_id',
    'form_user_image_key': 'file',
    'form_asset_image_key': 'file',
    'form_service_image_key': 'file',
}