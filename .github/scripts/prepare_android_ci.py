"""Create ignored Android configuration files in a fresh CI checkout."""

import json
import os
from pathlib import Path


def property_value(value: str) -> str:
    """Escape a single-line API key for Java properties and generated Kotlin strings."""
    if any(character in value for character in '\r\n"\\'):
        raise ValueError("API keys must be single-line values without quotes or backslashes")
    return value.replace("=", "\\=").replace(":", "\\:")


def main() -> None:
    local_properties = Path("local.properties")
    firebase_file = Path("app/google-services.json")
    if local_properties.exists() or firebase_file.exists():
        raise FileExistsError("CI preparation requires a fresh checkout; existing configuration was preserved")

    firebase_json = os.environ.get("GOOGLE_SERVICES_JSON", "")
    if firebase_json:
        firebase_config = json.loads(firebase_json)
    else:
        # Build-only values: no real Firebase project or credentials are included.
        firebase_config = {
            "project_info": {
                "project_number": "123456789012",
                "project_id": "planty-ci-placeholder",
                "storage_bucket": "planty-ci-placeholder.firebasestorage.app",
            },
            "client": [{
                "client_info": {
                    "mobilesdk_app_id": "1:123456789012:android:0123456789abcdef012345",
                    "android_client_info": {"package_name": "com.traffipart.polanty"},
                },
                "api_key": [{"current_key": "AIzaSy" + "0" * 33}],
            }],
            "configuration_version": "1",
        }

    keys = ("PLANT_NET_API_KEY", "PERENUAL_API_KEY")
    properties = "".join(f"{key}={property_value(os.environ.get(key, ''))}\n" for key in keys)
    local_properties.write_text(properties, encoding="utf-8")
    firebase_file.write_text(json.dumps(firebase_config, indent=2) + "\n", encoding="utf-8")
    print("CI configuration prepared with " + ("provided Firebase configuration" if firebase_json else "build-only placeholders"))


if __name__ == "__main__":
    main()
