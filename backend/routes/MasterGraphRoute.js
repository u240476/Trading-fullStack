const express = require("express");
const router = express.Router();
const JAVA_SERVICE_URL = process.env.JAVA_SERVICE_URL || "http://localhost:8080";


router.get("/", async (req, res) => {
    try {
        const { tickers } = req.query;

        const query = new URLSearchParams({ 
            tickers 
        }).toString();

        const endpoints = {
            ef: `${JAVA_SERVICE_URL}/graph-min-risk-for-return?${query}`,
            cal: `${JAVA_SERVICE_URL}/capital-allocation-line?${query}`,
            tp: `${JAVA_SERVICE_URL}/graph-tp-data?${query}`,
            mvp: `${JAVA_SERVICE_URL}/graph-mvp-data?${query}`
        };

        const [efResponse, calResponse, tpResponse, mvpResponse] =
            await Promise.all([
                fetch(endpoints.ef),
                fetch(endpoints.cal),
                fetch(endpoints.tp),
                fetch(endpoints.mvp)
            ]);
    
        if (!efResponse.ok || !calResponse.ok || !tpResponse.ok || !mvpResponse.ok) {
            return res.status(500).json({ error: "microservice error" });
        }

        const [efData, calData, tpData, mvpData] = await Promise.all([
            efResponse.json(),
            calResponse.json(),
            tpResponse.json(),
            mvpResponse.json()
        ]);

        return res.status(200).json({
            efData,
            calData,
            tpData,
            mvpData
        });

    } catch (err) {
        console.error(err);
        return res.status(500).json({ error: "server error" });
    }
});
module.exports = router;