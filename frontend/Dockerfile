FROM node:lts AS builder

WORKDIR /app
COPY package.json .
COPY yarn.lock .
RUN yarn --network-timeout 100000

###

FROM builder AS develop

ARG VITE_AVOGADOR_BACKEND_API_ADDRESS
ENV VITE_AVOGADOR_BACKEND_API_ADDRESS: ${VITE_AVOGADOR_BACKEND_API_ADDRESS}

ARG VITE_GOOGLE_CLIENT_ID
ENV VITE_GOOGLE_CLIENT_ID: ${VITE_GOOGLE_CLIENT_ID}

ARG VITE_GLITCHTIP_DSN
ENV VITE_GLITCHTIP_DSN: ${VITE_GLITCHTIP_DSN}

ARG VITE_CUSTOMER_DOMAINS
ENV VITE_CUSTOMER_DOMAINS: ${VITE_CUSTOMER_DOMAINS}

ARG VITE_AVOGADOR_BACKEND_WEBSOCKET_ADDRESS
ENV VITE_AVOGADOR_BACKEND_WEBSOCKET_ADDRESS: ${VITE_AVOGADOR_BACKEND_WEBSOCKET_ADDRESS}

ARG VITE_REACT_BASE_URL
ENV VITE_REACT_BASE_URL: ${VITE_REACT_BASE_URL}

COPY . .
RUN yarn build:production

ENV NODE_ENV production

###

FROM nginx AS staging
COPY --from=develop /app/dist /usr/share/nginx/html
COPY ./nginx.conf /etc/nginx/conf.d/default.conf
CMD ["nginx", "-g", "daemon off;"]

FROM develop AS obfuscated
ARG OBFUSCATE_DIRECTIVE 
RUN yarn run obfuscate -- $OBFUSCATE_DIRECTIVE

FROM nginx AS production
COPY --from=develop /app/dist /usr/share/nginx/html
COPY ./nginx.conf /etc/nginx/conf.d/default.conf
CMD ["nginx", "-g", "daemon off;"]
