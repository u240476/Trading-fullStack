document.addEventListener("DOMContentLoaded", () => {

    const portfolioData = [];

    const ctx = document.getElementById("riskReturnChart");

    const chart = new Chart(ctx, {
        type: "scatter",
        data: {
            datasets: [
                { label: "Stocks", data: [] },
                { label: "Minimum Variance Portfolio", data: [], pointBackgroundColor: "green" },
                { label: "Tangency Portfolio", data: [], pointBackgroundColor: "red" },
                { label: "Your Portfolio", data: [], pointBackgroundColor: "orange" }
            ]
        },
        options: {
            plugins: {
                tooltip: {
                    callbacks: {
                        label: function (context) {
                            const p = context.raw;
                            return `${p.ticker}: Return ${p.y.toFixed(2)}%, Risk ${p.x.toFixed(2)}%`;
                        }
                    }
                }
            },
            scales: {
                x: {
                    min: -5,
                    max: 15,
                    title: {
                        display: true,
                        text: "Standard Deviation (%)"
                    }
                },
                y: {
                    min: -5,
                    max: 15,
                    title: {
                        display: true,
                        text: "Expected Return (%)"
                    }
                }
            }
        }
    });


    
    function updateChart(datasetIndex, x, y, label) {
        chart.data.datasets[datasetIndex].data.push({
            x,
            y,
            ticker: label
        });

        chart.update();
    }

    function clearInputs(ids) {
        ids.forEach(id => {
            document.getElementById(id).value = "";
        });
    }


    document.getElementById("clear-graph-button").addEventListener("click", () => {

        chart.data.datasets.forEach(d => d.data = []);
        portfolioData.length = 0;

        chart.update();
    });


    document.getElementById("graph-form").addEventListener("submit", async (e) => {
        e.preventDefault();

        const ticker = document.getElementById("graph-ticker-input")
            .value
            .trim()
            .toUpperCase();

        if (!ticker || ticker.includes(",")) {
            alert("Please enter only ONE ticker.");
            return;
        }

        try {
            const res = await fetch(
                `http://localhost:3010/api/graph-data?ticker=${ticker}`
            );

            const data = await res.json();

            const stock = {
                ticker,
                expectedReturn: data.expectedReturn * 100,
                standardDeviation: data.standardDeviation * 100
            };

            portfolioData.push(stock);

            updateChart(
                0,
                stock.standardDeviation,
                stock.expectedReturn,
                stock.ticker
            );

            clearInputs(["graph-ticker-input"]);

        } catch (err) {
            console.error(err);
        }
    });


   
    document.getElementById("graph-mvp-form").addEventListener("submit", async (e) => {
        e.preventDefault();

        const tickers = document.getElementById("graph-mvp-tickers-input")
            .value
            .trim()
            .toUpperCase();

        if (!tickers) {
            alert("Please enter tickers.");
            return;
        }

        try {
            const res = await fetch(
                `http://localhost:3010/api/graph-mvp-data?tickers=${tickers}`
            );

            if (!res.ok) throw new Error(await res.text());

            const data = await res.json();

            const point = {
                portfolioReturn: data.portfolioReturn * 100,
                portfolioStandardDeviation: data.portfolioStandardDeviation * 100
            };

            portfolioData.push(point);

            updateChart(
                1,
                point.portfolioStandardDeviation,
                point.portfolioReturn,
                "MVP"
            );

            clearInputs(["graph-mvp-tickers-input"]);

        } catch (err) {
            console.error(err);
        }
    });


    document.getElementById("graph-tp-form").addEventListener("submit", async (e) => {
        e.preventDefault();

        const tickers = document.getElementById("graph-tp-tickers-input")
            .value
            .trim()
            .toUpperCase();

        if (!tickers) {
            alert("Please enter tickers.");
            return;
        }

        try {
            const res = await fetch(
                `http://localhost:3010/api/graph-tp-data?tickers=${tickers}`
            );

            if (!res.ok) throw new Error(await res.text());

            const data = await res.json();

            const point = {
                portfolioReturn: data.portfolioReturn * 100,
                portfolioStandardDeviation: data.portfolioStandardDeviation * 100
            };

            portfolioData.push(point);

            updateChart(
                2,
                point.portfolioStandardDeviation,
                point.portfolioReturn,
                "TP"
            );

            clearInputs(["graph-tp-tickers-input"]);

        } catch (err) {
            console.error(err);
        }
    });


    document.getElementById("graph-portfolio-form").addEventListener("submit", async (e) => {
        e.preventDefault();

        const tickers = document.getElementById("graph-portfolio-tickers-input")
            .value
            .trim()
            .split(",")
            .map(t => t.trim());

        const proportions = document.getElementById("graph-portfolio-proportions-input")
            .value
            .trim()
            .split(",")
            .map(n => n.trim());

        if (!tickers.length || !proportions.length) {
            alert("Please enter valid portfolio data.");
            return;
        }

        const params = new URLSearchParams({
            tickers: tickers.join(","),
            proportions: proportions.join(",")
        });

        try {
            const res = await fetch(
                `http://localhost:3010/api/graph-portfolio-data?${params.toString()}`
            );

            const data = await res.json();

            const point = {
                portfolioReturn: data.portfolioReturn * 100,
                portfolioStandardDeviation: data.portfolioStandardDeviation * 100
            };

            updateChart(
                3,
                point.portfolioStandardDeviation,
                point.portfolioReturn,
                "Your Portfolio"
            );

            clearInputs([
                "graph-portfolio-tickers-input",
                "graph-portfolio-proportions-input"
            ]);

        } catch (err) {
            console.error(err);
        }
    });

});