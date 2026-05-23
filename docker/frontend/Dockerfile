FROM nginx:alpine

MAINTAINER sadjier

COPY ./dist /usr/share/nginx/html
COPY nginx.conf /etc/nginx/nginx.conf
COPY cert/ /etc/nginx/ssl/

EXPOSE 8443

CMD ["nginx", "-g", "daemon off;"]