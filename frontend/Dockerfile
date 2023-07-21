FROM node:alpine AS develop

WORKDIR /app

COPY . .

RUN yarn
RUN yarn global add serve
RUN yarn build

ENV NODE_ENV production

CMD ["serve", "-s", "dist"]

###

FROM develop as production
ARG OBFUSCATE_DIRECTIVE 
RUN yarn run obfuscate -- $OBFUSCATE_DIRECTIVE
