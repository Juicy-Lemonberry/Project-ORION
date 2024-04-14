from quart import Quart

app = Quart(__name__)

# Import routes after creating the app to avoid circular imports
from project_orion_server import get_routes, post_routes
