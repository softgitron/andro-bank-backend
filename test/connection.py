import requests

ADDRESS = "http://localhost:8080"


class Connection:
    headers = {"X-Auth-Token": ""}

    def new_request(self, request_type, path, payload="", authentication=False):
        full_path = ADDRESS + path
        if request_type == "POST":
            if authentication:
                return requests.post(url=full_path, data=payload, headers=self.headers)
            else:
                r = requests.post(url=full_path, data=payload)
                if "X-Auth-Token" in r.headers:
                    self.headers = {"X-Auth-Token": r.headers["X-Auth-Token"]}
                return r

        elif request_type == "GET":
            if authentication:
                return requests.get(url=full_path, headers=self.headers, data=payload)
            else:
                return requests.get(url=full_path, data=payload)

        elif request_type == "PATCH":
            if authentication:
                return requests.patch(url=full_path, headers=self.headers, data=payload)
            else:
                return requests.patch(url=full_path, data=payload)

        elif request_type == "DELETE":
            if authentication:
                return requests.delete(
                    url=full_path, headers=self.headers, data=payload
                )
            else:
                return requests.delete(url=full_path, data=payload)
