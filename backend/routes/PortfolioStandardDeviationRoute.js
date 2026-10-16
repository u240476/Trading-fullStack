const express = require("express");
const router = express.Router();
const JAVA_SERVICE_URL = process.env.JAVA_SERVICE_URL || "http://localhost:8080";


router.get("/", async (req, res) => {
    try {
        const { tickers, proportions } = req.query;

    const params = new URLSearchParams();

    if (tickers) params.append("tickers", tickers);
    if (proportions) params.append("proportions", proportions);

    const response = await fetch(
        `${JAVA_SERVICE_URL}/portfolio-standard-deviation?${params.toString()}`
    );

        const text = await response.text(); 

        console.log("RAW SPRING RESPONSE:", text);

        if (!response.ok) {
            return res.status(500).json({ error: "microservice error" });
        }

        const data = JSON.parse(text);

        return res.status(200).json(data);

    } catch (err) {
        console.error(err);
        return res.status(500).json({ error: "server error" });
    }
});
module.exports = router;