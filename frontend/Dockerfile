# Build Stage
FROM node:20-alpine AS build
WORKDIR /app

# Accept API URL as build arg (so the frontend knows how to communicate with backend)
ARG VITE_API_URL
ENV VITE_API_URL=${VITE_API_URL}

# Copy package files and install dependencies
COPY package.json package-lock.json ./
RUN npm ci

# Copy the rest of the application and build
COPY . .
RUN npm run build

# Run Stage
FROM nginx:alpine
# Remove default nginx config
RUN rm /etc/nginx/conf.d/default.conf
# Copy custom nginx config
COPY nginx.conf /etc/nginx/conf.d/
# Copy built assets to nginx root
COPY --from=build /app/dist /usr/share/nginx/html

EXPOSE 80
CMD ["nginx", "-g", "daemon off;"]
