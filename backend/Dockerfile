FROM node:20-bookworm-slim
WORKDIR /app

# Root-level deps (sqlite3, bcrypt, jsonwebtoken, ...)
COPY package*.json ./
RUN npm install --omit=dev

# Backend code + its own deps, if it has a package.json
COPY backend/ ./backend/
RUN if [ -f backend/package.json ]; then cd backend && npm install --omit=dev; fi

# Seed database, copied into the volume on first run only
COPY database.db /seed/database.db
COPY backend/entrypoint.sh /entrypoint.sh
RUN chmod +x /entrypoint.sh

ENV NODE_ENV=production
ENV PORT=3000
ENV DB_PATH=/data/database.db
EXPOSE 3000

ENTRYPOINT ["/entrypoint.sh"]
# CHANGE if your entry file is different (app.js, index.js, src/server.js ...)
CMD ["node", "backend/server.js"]