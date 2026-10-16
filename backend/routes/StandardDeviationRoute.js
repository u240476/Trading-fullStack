const express = require("express");
console.log("LOADING STD DEV ROUTE FILE");
const router = express.Router();
const JAVA_SERVICE_URL = process.env.JAVA_SERVICE_URL || "http://localhost:8080";


router.get("/", async (req, res) => {
    try {
        const { tickers } = req.query;

        const response = await fetch(
            `${JAVA_SERVICE_URL}/standard-deviation?tickers=${encodeURIComponent(tickers)}`
        );

        const text = await response.text();

        console.log("RAW SPRING RESPONSE:", text);

        if (!response.ok) {
            console.error("BACKEND ERROR:", text);
            throw new Error(text);
        }

        const data = JSON.parse(text);

        return res.status(200).json(data);

    } catch (err) {
        console.error(err);
        return res.status(500).json({ error: "server error" });
    }
});
console.log("STD DEV ROUTE READY");
module.exports = router;