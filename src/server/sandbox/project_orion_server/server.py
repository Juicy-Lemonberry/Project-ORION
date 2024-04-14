from config import SERVER_CONFIG
from project_orion_server import app

if __name__ == '__main__':
    app.run(host=SERVER_CONFIG['host'], port=SERVER_CONFIG['port'], debug=True)
