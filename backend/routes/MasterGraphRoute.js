const express = require("express");
const router = express.Router();

router.get("/", async (req, res) => {
    try {
        const { tickers } = req.query;

        const query = new URLSearchParams({ 
            tickers 
        }).toString();

        const endpoints = {
            ef: `http://localhost:8080/graph-min-risk-for-return?${query}`,
            cal: `http://localhost:8080/capital-allocation-line?${query}`,
            tp: `http://localhost:8080/graph-tp-data?${query}`,
            mvp: `http://localhost:8080/graph-mvp-data?${query}`
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