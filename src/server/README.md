# 🌠 Project Orion REST API usage

## 💡 Overview

This document outlines the API endpoints for the Asset Management System. The system allows for managing assets, asset types, checklists, maintenance records, and user information, along with the capability to upload and retrieve images related to assets, services, and users.

## 🟩 GET Requests (Data Retrieval)

### 🟢 Retrieve Assets (`/assets`)

- **Purpose**: Fetches asset details based on query parameters.
- **Query Parameters**:
  - `asset_id` (int, optional): The unique ID of the asset.
  - `asset_name` (string, optional): Name of the asset.
  - `type_id` (int, optional): Type ID of the asset.
  - `is_active` (int, optional): Whether the asset is in service (`1`) or not (`0`).
- **Returns**: JSON list of assets with `date_added` and `date_last_serviced` DateTime in [UNIX Epoch Time](https://www.epochconverter.com/) format.

**Example Usage**

Request to retrieve assets of type 10 and not in service:

```
/assets?type_id=10&is_active=0
```

**Example Return**

```json
[
  {
    "asset_desc": "Airbus A320neo",
    "asset_id": 41,
    "asset_name": "AIR101SG",
    "date_added": 1362758400,
    "date_last_serviced": null,
    "is_active": 0,
    "type_id": 10
  },
  {
    "asset_desc": "Airbus A380",
    "asset_id": 43,
    "asset_name": "AIR103SG",
    "date_added": 1301414400,
    "date_last_serviced": null,
    "is_active": 0,
    "type_id": 10
  }
]
```
---

### 🟢 Retrieve Asset Types (`/asset_types`)

- **Purpose**: Fetches asset type details.
- **Query Parameters**:
  - `type_id` (int, optional): The unique ID of the asset type.
  - `type_desc` (string, optional): Description of the asset type.
- **Returns**: JSON list of asset types.

**Example Usage**

Request to retrieve asset type details by type ID:

```
/asset_types?type_id=10
```

**Example Return**

```json
[
    {
        "type_desc": "Aircraft",
        "type_id": 10
    }
]
```
---

### 🟢 Retrieve Checklists (`/checklists`)

- **Purpose**: Fetches checklists for assets.
- **Query Parameters**:
  - `checklist_id` (int, optional): The unique ID of the checklist.
  - `asset_id` (int, optional): The unique ID of the asset associated with the checklist.
- **Returns**: JSON list of checklists.

**Example Usage**

Request to retrieve checklists associated with a specific asset ID:

```
/checklists?asset_id=43
```

**Example Return**

```json
[
    {
        "asset_id": 43,
        "checklist_desc": "Check the upper deck for structural integrity and emergency exit functionality.",
        "checklist_id": 14,
        "checklist_title": "Upper Deck Inspection"
    },
    {
        "asset_id": 43,
        "checklist_desc": "Ensure all passenger and cargo areas on the lower deck are secure and undamaged.",
        "checklist_id": 15,
        "checklist_title": "Lower Deck Inspection"
    },
    {
        "asset_id": 43,
        "checklist_desc": "Conduct detailed engine checks for all four engines, including thrust and efficiency tests.",
        "checklist_id": 16,
        "checklist_title": "Engine Performance Test"
    }
]
```
---

### 🟢 Retrieve Maintenance Records (`/records`)

- **Purpose**: Fetches maintenance records.
- **Query Parameters**:
  - `service_id` (int, optional): The unique ID of the maintenance record.
  - `asset_id` (int, optional): The unique ID of the asset.
- **Returns**: JSON list of maintenance records with date_recorded and date_serviced DateTime in [UNIX Epoch Time](https://www.epochconverter.com/) format

**Example Usage**

Request to retrieve maintenance records for a specific asset ID:

```
/records?asset_id=43
```

**Example Return**

```json
[
    {
        "asset_id": 43,
        "date_recorded": 1711175092,
        "date_serviced": null,
        "service_desc": "Engine Diagnostic",
        "service_id": 18,
        "user_id": 69
    },
    {
        "asset_id": 43,
        "date_recorded": 1711175092,
        "date_serviced": null,
        "service_desc": "Hydraulic System Check",
        "service_id": 23,
        "user_id": 1206
    }
]
```
---

### 🟢 Retrieve Users (`/users`)

- **Purpose**: Fetches user details.
- **Query Parameters**:
  - `user_id` (int, optional): The unique ID of the user.
  - `user_name` (string, optional): Name of the user.
- **Returns**: JSON list of users.

**Example Usage**

Request to retrieve user details by user ID:

```
/users?user_id=1206
```

**Example Return**

```json
[
    {
        "user_id": 1206,
        "user_name": "Mahiru Shiina"
    }
]
```
---

## 🟩 GET Requests (Image Download)

### 🟢 Serve Image (`/img/<category>/<filename>`)

- **Purpose**: Serves an image from a specified category and filename.
- **URL Parameters**:
  - `category` (string, required): Category of the image (`asset`, `service`, `user`).
  - `filename` (string, required): Filename of the image.
- **Returns**: The requested image or a fallback image if not found. (see `/default_images` to preview fallback images)

## 🟨 POST Requests (Data Adding and Updating*)
*Applicable only for the following endpoints: `/assets`, `/checklists`, `/records`,

### 🟡 Add a New Asset (`/assets`) ✅ Auto-updates on existing asset_id conflict

- **Purpose**: Adds a new asset to the system, otherwise, updates an existing asset based on the provided `asset_id`.
- **Body** (JSON):
  - `asset_id` (int, optional): Preferred ID for the asset. Will fallback to auto-increment if taken or not provided.
  - `asset_name` (string, required): Name of the asset.
  - `type_id` (int, required): Type ID of the asset.
  - `asset_desc` (string, required): Description of the asset.
  - `date_added` (int, required): The date the asset was added, in UNIX Epoch Time format.
  - `date_last_serviced` (int, optional): The date the asset was last serviced, in UNIX Epoch Time format. Nullable.
  - `is_active` (int, required): Indicates if the asset is active (`1`) or not (`0`).
- **Returns**: JSON object indicating success and the `asset_id` of the newly added asset.

**Example POST JSON String** (use `null` to auto-increment `asset_id`)

```json
{
  "asset_id": null,
  "asset_name": "Human soul Maintenance Guide Book",
  "type_id": 69,
  "asset_desc": "The Angel Next Door spoils me Rotten",
  "date_added": 1545250842,
  "is_active": 1
}
```

**Example Return JSON String**

```json
{
  "asset_id": 1207,
  "message": "Asset added successfully"
}
```
---
### 🟡 Add a New Asset Type (`/asset_types`) ❌ Does not update existing type_id on conflict

- **Purpose**: Adds a new asset type to the system.
- **Body** (JSON):
  - `type_id` (int, optional): Preferred ID for the asset type. Will fallback to auto-increment if taken or not provided.
  - `type_desc` (string, required): Description of the asset type.
- **Returns**: JSON object indicating success and the `type_id` of the newly added asset type.

**Example POST JSON String**

```json
{
  "type_id": 69,
  "type_desc": "The best book in the world"
}
```

**Example Return JSON String**

```json
{
  "message": "Asset Type added successfully",
  "type_id": 69
}
```
---

### 🟡 Add a New Checklist (`/checklists`) ✅ Auto-updates on existing checklist_id conflict

- **Purpose**: Adds a new checklist for an asset.
- **Body** (JSON):
  - `asset_id` (int, required): The ID of the asset the checklist belongs to.
  - `checklist_title` (string, required): Title of the checklist.
  - `checklist_desc` (string, required): Description or contents of the checklist.
- **Returns**: JSON object indicating success and the `checklist_id` of the newly added checklist.

**Example POST JSON String**

```json
{
  "asset_id": 1207,
  "checklist_title": "Checkout the manga adaptation",
  "checklist_desc": "Check out the manga adaptation of the light novel series."
}
```

**Example Return JSON String**

```json
{
  "checklist_id": 54,
  "message": "Checklist added successfully"
}
```
---

### 🟡 Add a New Maintenance Record (`/records`) ✅ Auto-updates on existing service_id conflict

- **Purpose**: Adds a new maintenance record for an asset.
- **Body** (JSON):
  - `asset_id` (int, required): The ID of the asset the record is for.
  - `user_id` (int, required): The ID of the user who performed the maintenance.
  - `service_desc` (string, required): Description of the maintenance performed.
  - `date_recorded` (int, required): The date the record was created, in UNIX Epoch Time format.
  - `date_serviced` (int, optional): The date the service was performed, in UNIX Epoch Time format. Leave empty if not yet serviced.
- **Returns**: JSON object indicating success and the `service_id` of the newly added record.

**Example POST JSON String**

```json
{
  "asset_id": 1207,
  "user_id": 69,
  "service_desc": "Drop the biggest merch purchase in history.",
  "date_recorded": 1617235200,
  "date_serviced": 1617321600
}
```

**Example Return JSON String**

```json
{
  "message": "Record added successfully",
  "service_id": 28
}
```
---

### 🟡 Add a New User (`/users`) ❌ Does not update existing user_id on conflict

- **Purpose**: Adds a new user to the system.
- **Body** (JSON):
  - `user_id` (int, optional): Preferred ID for the user. Will fallback to auto-increment if taken or not provided.
  - `user_name` (string, required): Name of the user.
- **Returns**: JSON object indicating success and the `user_id` of the newly added user.

**Example POST JSON String**

```json
{
  "user_id": null,
  "user_name": "Ninomae Ina'nis"
}
```

**Example Return JSON String**

```json
{
  "message": "User added successfully",
  "user_id": 1208
}
```
---

## 🟨 POST Requests (Image Upload)
---
### 🟡 Upload User Avatar (`/uploads/img/user`)

- **Purpose**: Uploads and stores an avatar image for a user.
- **Headers**:
  - `user_image_id_header`: Header specifying the user ID.
- **Form Data**:
  - File: The image file to upload.
- **Restrictions**: Images must be of type `png`, `jpg`, or `jpeg`. Max file size is 100 MB.
- **Returns**: JSON object with upload status and filename.
---
### 🟡 Upload Asset Image (`/uploads/img/asset`)

- **Purpose**: Uploads and stores a preview image for an asset.
- **Headers**:
  - `asset_image_id_header`: Header specifying the asset ID.
- **Form Data**:
  - File: The image file to upload.
- **Restrictions**: Images must be of type `png`, `jpg`, or `jpeg`. Max file size is 500 MB.
- **Returns**: JSON object with upload status and filename.
---
### 🟡 Upload Service Image (`/uploads/img/service`)

- **Purpose**: Uploads and stores an image taken during asset's service or inspection.
- **Headers**:
  - `service_image_id_header`: Header specifying the maintenance or service ID.
- **Form Data**:
  - File: The image file to upload.
- **Restrictions**: Images must be of type `png`, `jpg`, or `jpeg`. Max file size is 500 MB.
- **Returns**: JSON object with upload status and filename.
---


## 🟥 DELETE Requests (Data Removal)

### 🔴 Delete an Asset (`/assets/<asset_id>`)

- **Purpose**: Deletes a specified asset from the system based on `asset_id`.
- **URL Parameter**:
  - `asset_id` (int, required): The unique ID of the asset to delete.
- **Returns**: JSON object indicating success of deletion, or an error if the asset does not exist.

**Example Usage**

Request to delete an asset with asset_id = 1207:

```
DELETE /assets/1207
```

**Example Return JSON String**

```json
{
  "message": "Asset deleted successfully"
}
```
---

### 🔴 Delete a Checklist (`/checklists/<checklist_id>`)

- **Purpose**: Deletes a specified checklist from the system based on `checklist_id`.
- **URL Parameter**:
  - `checklist_id` (int, required): The unique ID of the checklist to delete.
- **Returns**: JSON object indicating success of deletion, or an error if the checklist does not exist.

**Example Usage**

Request to delete a checklist with checklist_id = 54:

```
DELETE /checklists/54
```

**Example Return JSON String**

```json
{
  "message": "Checklist deleted successfully"
}
```
---

### 🔴 Delete a Maintenance Record (`/records/<service_id>`)

- **Purpose**: Deletes a specified maintenance record from the system based on `service_id`.
- **URL Parameter**:
  - `service_id` (int, required): The unique ID of the maintenance record to delete.
- **Returns**: JSON object indicating success of deletion, or an error if the maintenance record does not exist.

**Example Usage**

Request to delete a maintenance record with service_id = 28:

```
DELETE /records/28
```

**Example Return JSON String**

```json
{
  "message": "Record deleted successfully"
}
```
---

## ▶️ Running the Server

To start the server, ensure you have the necessary environment and configuration set up, then run:

```bash
python $ python ~/inf2007-team48-2024/src/server/orion_server.py
