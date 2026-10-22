/*document.addEventListener("DOMContentLoaded", () => {

    const ctx = document.getElementById("portfolio-diagnosticsChart");

    const chart = new Chart(ctx, {
        type: "scatter",
        data: {
            datasets: [
{
    label: "Efficient Frontier",
    data: [],
    showLine: true,
    borderColor: "#00d4ff",
    borderWidth: 4,
    pointRadius: 0,
    tension: 0.15,
    order: 0
},
{
    label: "Capital Allocation Line",
    data: [],
    showLine: true,
    borderColor: "#8b949e",
    borderDash: [6, 6],
    borderWidth: 2,
    pointRadius: 0,
    order: 1
},
{
    label: "Minimum Variance Portfolio",
    data: [],
    pointBackgroundColor: "#00e676",
    pointBorderColor: "#0d1117",
    pointBorderWidth: 3,
    pointRadius: 7,
    pointHoverRadius: 10,
    order: 10
},
{
    label: "Tangency Portfolio",
    data: [],
    pointBackgroundColor: "#ffb300",
    pointBorderColor: "#0d1117",
    pointBorderWidth: 3,
    pointRadius: 8,
    pointHoverRadius: 11,
    order: 11
},
{
    label: "Stocks",
    data: [],
    pointBackgroundColor: "#ff5252",
    pointBorderColor: "#0d1117",
    pointBorderWidth: 3,
    pointRadius: 8,
    pointHoverRadius: 11,
    order: 11
},
{
    label: "Users Portfolio",
    data: [],
    pointBackgroundColor: "#b388ff",
    pointBorderColor: "#0d1117",
    pointBorderWidth: 3,
    pointRadius: 8,
    pointHoverRadius: 11,
    order: 11
}

]
        },
        options: {
            plugins: {
                tooltip: {
                    callbacks: {
                        label: (c) => {
                        const p = c.raw;
                        return `${p.ticker ?? c.dataset.label}: ${p.x.toFixed(2)}% risk, ${p.y.toFixed(2)}% return`;
                        }   
                    }
                }
            },
            scales: {
                x: {
                    title: { display: true, text: "Standard Deviation (%)" }
                },
                y: {
                    title: { display: true, text: "Expected Return (%)" }
                }
            }
        }
    });

    function parseCSV(input) {
        return input
            .split(/[\s,]+/)
            .map(x => x.trim())
            .filter(Boolean);
    }

    function setDataset(index, data) {
        chart.data.datasets[index].data = data;
    }

    document.getElementById("clear-graph-button")
        .addEventListener("click", () => {

            chart.data.datasets.forEach(d => {
                d.data = [];
            });

            chart.update();
        });

    function renderMasterGraph(data, tickers) {

        setDataset(0, data.efData.map(p => ({
            x: p.minPortfolioStandardDeviation  * 100,
            y: p.minPortfolioReturn  * 100
        })));

        setDataset(1, data.calData.map(p => ({
            x: p.risk  * 100,
            y: p.expectedReturn * 100
        })));

        setDataset(2, [{
            x: data.mvpData.portfolioStandardDeviation * 100,
            y: data.mvpData.portfolioReturn * 100,
            ticker: "MVP"
        }]);

        setDataset(3, [{
            x: data.tpData.portfolioStandardDeviation * 100,
            y: data.tpData.portfolioReturn * 100,
            ticker: "TP"
        }]);
        const stocks = Array.isArray(data.StockData) ? data.StockData : [data.StockData];
        setDataset(4, stocks.map((s, i) => ({
            x: s.stockStandardDeviation * 100,
            y: s.stockReturn * 100,
            ticker: s.ticker ?? tickers[i]
        })));

        setDataset(5, [{
            x: data.userPortfolioData.portfolioStandardDeviation * 100,
            y: data.userPortfolioData.portfolioReturn * 100,
            ticker: "User Portfolio"
        }]);

        chart.update();
    }

    document.getElementById("portfolio-diagnostics-graph-form")
    .addEventListener("submit", async (e) => {

            e.preventDefault();

            const input = document.getElementById("portfolio-diagnostics-graph-tickers-input").value;

            const amounts = parseCSV(document.getElementById("portfolio-diagnostics-returns-proportions-input").value);

            if (amounts.length !== tickers.length) {
                alert("Enter one amount per ticker");
                return;
            }
            if (!input.trim()) {
                alert("Please enter tickers");
                return;
            }

            const tickers = parseCSV(input);

            try {
                const params = new URLSearchParams({
                    tickers: tickers.join(","),
                    amounts: amounts.join(",")
                });

                const res = await fetch(
                    `/api/master-graph?${params.toString()}`
                );

                if (!res.ok) {
                    throw new Error(await res.text());
                }

                const data = await res.json();
                document.getElementById("master-graph-tickers-input").value = "";
                renderMasterGraph(data, tickers);

            } catch (err) {
                console.error("Master graph error:", err);
            }
        });
});*/
document.addEventListener("DOMContentLoaded", () => {

    const ctx = document.getElementById("portfolio-diagnosticsChart");

    const chart = new Chart(ctx, {
        type: "scatter",
        data: {
            datasets: [
{
    label: "Efficient Frontier",
    data: [],
    showLine: true,
    borderColor: "#00d4ff",
    borderWidth: 4,
    pointRadius: 0,
    tension: 0.15,
    order: 0
},
{
    label: "Capital Allocation Line",
    data: [],
    showLine: true,
    borderColor: "#8b949e",
    borderDash: [6, 6],
    borderWidth: 2,
    pointRadius: 0,
    order: 1
},
{
    label: "Minimum Variance Portfolio",
    data: [],
    pointBackgroundColor: "#00e676",
    pointBorderColor: "#0d1117",
    pointBorderWidth: 3,
    pointRadius: 7,
    pointHoverRadius: 10,
    order: 10
},
{
    label: "Tangency Portfolio",
    data: [],
    pointBackgroundColor: "#ffb300",
    pointBorderColor: "#0d1117",
    pointBorderWidth: 3,
    pointRadius: 8,
    pointHoverRadius: 11,
    order: 11
},
{
    label: "Stocks",
    data: [],
    pointBackgroundColor: "#ff5252",
    pointBorderColor: "#0d1117",
    pointBorderWidth: 3,
    pointRadius: 6,
    pointHoverRadius: 9,
    order: 12
},
{
    label: "Your Portfolio",
    data: [],
    pointBackgroundColor: "#b388ff",
    pointBorderColor: "#0d1117",
    pointBorderWidth: 3,
    pointRadius: 8,
    pointHoverRadius: 11,
    order: 13
}
]
        },
        options: {
            plugins: {
                tooltip: {
                    callbacks: {
                        label: (c) => {
                            const p = c.raw;
                            const name = p.ticker ?? c.dataset.label;
                            return `${name}: ${p.y.toFixed(2)}% return, ${p.x.toFixed(2)}% risk`;
                        }
                    }
                }
            },
            scales: {
                x: {
                    title: { display: true, text: "Standard Deviation (%)" }
                },
                y: {
                    title: { display: true, text: "Expected Return (%)" }
                }
            }
        }
    });

    function parseCSV(input) {
        return input
            .split(/[\s,]+/)
            .map(x => x.trim())
            .filter(Boolean);
    }

    function setDataset(index, data) {
        chart.data.datasets[index].data = data;
    }

    async function fetchJSON(url) {
        const res = await fetch(url);
        if (!res.ok) {
            throw new Error(await res.text());
        }
        return res.json();
    }

    document.getElementById("clear-graph-button")
        .addEventListener("click", () => {

            chart.data.datasets.forEach(d => {
                d.data = [];
            });

            chart.update();
        });

    function renderDiagnosticsGraph(master, portfolio, stocks, tickers) {

        setDataset(0, master.efData.map(p => ({
            x: p.minPortfolioStandardDeviation * 100,
            y: p.minPortfolioReturn * 100
        })));

        setDataset(1, master.calData.map(p => ({
            x: p.risk * 100,
            y: p.expectedReturn * 100
        })));

        setDataset(2, [{
            x: master.mvpData.portfolioStandardDeviation * 100,
            y: master.mvpData.portfolioReturn * 100,
            ticker: "MVP"
        }]);

        setDataset(3, [{
            x: master.tpData.portfolioStandardDeviation * 100,
            y: master.tpData.portfolioReturn * 100,
            ticker: "TP"
        }]);

        setDataset(4, stocks.map((s, i) => ({
            x: s.standardDeviation * 100,
            y: s.expectedReturn * 100,
            ticker: tickers[i]
        })));

        setDataset(5, [{
            x: portfolio.portfolioStandardDeviation * 100,
            y: portfolio.portfolioReturn * 100,
            ticker: "Your Portfolio"
        }]);

        chart.update();
    }

    document.getElementById("portfolio-diagnostics-graph-form")
        .addEventListener("submit", async (e) => {

            e.preventDefault();

            const tickerInput = document.getElementById("portfolio-diagnostics-graph-tickers-input");
            const amountInput = document.getElementById("portfolio-diagnostics-returns-proportions-input");

            if (!tickerInput.value.trim() || !amountInput.value.trim()) {
                alert("Please enter tickers and amounts");
                return;
            }

            const tickers = parseCSV(tickerInput.value).map(t => t.toUpperCase());
            const amounts = parseCSV(amountInput.value).map(Number);

            if (amounts.length !== tickers.length || amounts.some(a => !Number.isFinite(a))) {
                alert("Enter one numeric amount per ticker");
                return;
            }

            // turn amounts (e.g. 1000,2000,30000) into weights that sum to 1
            const total = amounts.reduce((a, b) => a + b, 0);
            const weights = amounts.map(a => a / total);

            try {
    const [master, portfolio, stocks] = await Promise.all([
        fetchJSON(`/api/master-graph?${new URLSearchParams({
            tickers: tickers.join(",")
        })}`),
        fetchJSON(`/api/graph-portfolio-data?${new URLSearchParams({
            tickers: tickers.join(","),
            proportions: weights.join(",")
        })}`),
        Promise.all(tickers.map(t =>
            fetchJSON(`/api/graph-data?${new URLSearchParams({ ticker: t })}`)
        ))
    ]);

    tickerInput.value = "";
    amountInput.value = "";
    renderDiagnosticsGraph(master, portfolio, stocks, tickers);

} catch (err) {
    console.error("Portfolio diagnostics error:", err);
}
        });
});