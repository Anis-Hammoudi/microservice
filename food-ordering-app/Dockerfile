# Stage 1: Build the Angular app using a Node.js image
FROM node:20-alpine AS builder

WORKDIR /app

COPY package*.json ./
RUN npm install

COPY . .

RUN npm run build
RUN ls -l /app/dist/food-ordering-app


# Stage 2: Serve the built app from a lightweight Nginx server
FROM nginx:stable-alpine

COPY --from=builder /app/dist/food-ordering-app/browser/ /usr/share/nginx/html/

COPY nginx.conf /etc/nginx/conf.d/default.conf

EXPOSE 80

CMD ["nginx", "-g", "daemon off;"]
