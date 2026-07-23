# # Stage 1: Build the application
# FROM node:24.8.0 AS build
# WORKDIR /usr/src/app
# COPY package.json /usr/src/app
# RUN npm install --force
# COPY . /usr/src/app

# # ✅ Clean Angular build cache and node_modules leftovers
# RUN rm -rf .angular/cache dist && npm cache clean --force

# RUN mkdir -p /etc/certi
# RUN npm run build

# # Stage 2: Create the final image
# FROM nginx:1.17.7-alpine

# # Copy SSL/TLS certificate files from Docker volume
# COPY domain.crt /etc/certi/domain.crt
# COPY domain.key /etc/certi/domain.key

# # Copy built Angular app files to Nginx HTML directory
# COPY --from=build /usr/src/app/dist/SavbillGui/browser /usr/share/nginx/html

# # Copy Nginx configuration file for HTTP
# COPY nginx-http.conf /etc/nginx/conf.d/default.conf

# # Copy Nginx configuration file for HTTPS
# COPY nginx-https.conf /etc/nginx/conf.d/https.conf

# # Configure ports
# EXPOSE 80
# EXPOSE 443

# # Start Nginx
# CMD ["/bin/sh", "-c", "envsubst < /usr/share/nginx/html/assets/env.template.js > /usr/share/nginx/html/assets/env.js && exec nginx -g 'daemon off;'"]

# ------------------------
# Stage 1 : Angular Build
# ------------------------
FROM node:24.8.0 AS build
WORKDIR /usr/src/app

COPY package.json package-lock.json ./
RUN npm install --force

COPY . .

# Clean cache (optional)
RUN rm -rf .angular/cache dist && npm cache clean --force

# Build Angular
RUN npm run build

# ------------------------
# Stage 2 : NGINX Runtime
# ------------------------
FROM nginx:1.25-alpine

# Create assets folder (important)
RUN mkdir -p /usr/share/nginx/html/assets

# Copy build output
COPY --from=build /usr/src/app/dist/SavbillGui/browser /usr/share/nginx/html

# Copy SSL certificate
COPY domain.crt /etc/certi/domain.crt
COPY domain.key /etc/certi/domain.key

# Copy Nginx config
COPY nginx-http.conf /etc/nginx/conf.d/default.conf
COPY nginx-https.conf /etc/nginx/conf.d/https.conf

# Generate env.js dynamically at container start
CMD ["/bin/sh", "-c", "envsubst < /usr/share/nginx/html/assets/env.template.js > /usr/share/nginx/html/assets/env.js && exec nginx -g 'daemon off;'"]

EXPOSE 80
EXPOSE 443
