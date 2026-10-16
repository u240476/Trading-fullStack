const express = require("express");
const router = express.Router();
const JAVA_SERVICE_URL = process.env.JAVA_SERVICE_URL || "http://localhost:8080";


router.get("/", async (req, res) => {
    try {
        const { tickers } = req.query;

        const response = await fetch(
            `${JAVA_SERVICE_URL}/mvp?tickers=${encodeURIComponent(tickers)}`
        );

        const text = await response.text(); 

        console.log("RAW SPRING RESPONSE:", text);

        if (!response.ok) {
            console.error("SPRING ERROR:", text);
            return res.status(500).json({ error: "microservice error", details: text});
        }

        const data = JSON.parse(text);

        return res.status(200).json(data);

    } catch (err) {
        
        console.error(err);
        return res.status(500).json({ error: "server error", details: text });
    }
});

module.exports = router;
