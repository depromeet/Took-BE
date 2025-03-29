#/bin/bash

echo "[aws configure --profile took] 필요!!"

cd ./lambda || exit

sam build -t template.yaml
sam deploy --no-confirm-changeset --resolve-s3 --profile took

cd ..

exit
