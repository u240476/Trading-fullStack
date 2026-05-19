const express = require("express");
const router = express.Router();

router.get("/", async (req, res) => {
    try {
        const { ticker } = req.query;

        const response = await fetch(
            `http://localhost:8080/price-return?ticker=${encodeURIComponent(ticker)}`
        );

        const text = await response.text(); 
        if(text){
        console.log("RAW SPRING RESPONSE OF PRICE DATA SUCCESFUL:");
        }

        if (!response.ok) {
            return res.status(500).json({ error: "microservice error" });
        }

        const data = JSON.parse(text);

        return res.status(200).json(data);

    } catch (err) {
        console.error(err);
        return res.status(500).json({error: err.message});
    }
});
module.exports = router;