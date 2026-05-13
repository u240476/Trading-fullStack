const express = require("express");
console.log("LOADING STD DEV ROUTE FILE");
const router = express.Router();

router.get("/", async (req, res) => {
    try {
        const { tickers } = req.query;

        const response = await fetch(
            `http://localhost:8080/standard-deviation?tickers=${encodeURIComponent(tickers)}`
        );

        const text = await response.text(); 

        console.log("RAW SPRING RESPONSE:", text);

        if (!response.ok) {
            const errText = await response.text();
            console.error("BACKEND ERROR:", errText);
            throw new Error(errText);
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