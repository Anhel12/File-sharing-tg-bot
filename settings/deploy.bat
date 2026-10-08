pushd D:\\programming\Java\File-sharing-tg-bot\

call git checkout main
call git pull origin main

call docker compose -f docker-compose.yaml --env-file .env down --timeout=60 --remove-orphans
call docker compose -f docker-compose.yaml --env-file .env up --build --detach

popd