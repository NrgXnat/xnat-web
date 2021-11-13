#!/bin/bash

pwd
curl -X DELETE "http://10.1.9.183/xapi/anonymize/scripts" \
-H "accept: */*" \
-u admin:admin

curl -X POST "http://10.1.9.183/xapi/anonymize/scripts" \
-u admin:admin \
-H "accept: */*" \
-H "Content-Type: application/json" \
--data-binary "@root.json"
