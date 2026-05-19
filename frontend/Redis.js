import { createClient } from "redis";

export const client = createClient({
  url: "redis://alice:foobared@awesome.redis.server:6380",
});

client.on("error", err => console.log("Redis Error", err));

export async function connectRedis() {
  if (!client.isOpen) {
    await client.connect();
    console.log("Redis connected");
  }
}