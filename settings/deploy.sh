#!/bin/bash

ENV_FILE="./.env"

pushd ~/File-sharing-tg-bot/ || exit

git checkout main
git pull origin main

docker compose -f docker-compose.yml --env-file $ENV_FILE down --timeout=60 --remove-orphans
docker compose -f docker-compose.yml --env-file $ENV_FILE up --build --detach

popd || exit