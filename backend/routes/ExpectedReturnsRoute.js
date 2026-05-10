const express = require("express");
const router = express.Router();

router.get("/expected-returns", async (req, res) => {
    try {
        const { tickers } = req.query;

        const response = await fetch(
            `http://localhost:8080/expected-return?tickers=${encodeURIComponent(tickers)}`
        );

        const text = await response.text(); // 👈 important debug step

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