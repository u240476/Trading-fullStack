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
    function frontierReturnAtRisk(efData, mvp, risk) {
    const upper = efData
        .filter(p => p.minPortfolioReturn >= mvp.portfolioReturn)
        .map(p => ({ risk: p.minPortfolioStandardDeviation, ret: p.minPortfolioReturn }))
        .sort((a, b) => a.risk - b.risk);

    if (upper.length < 2 || risk < upper[0].risk || risk > upper[upper.length - 1].risk) {
        return null;   
    }

    for (let i = 1; i < upper.length; i++) {
        if (risk <= upper[i].risk) {
            const a = upper[i - 1];
            const b = upper[i];
            if (b.risk === a.risk) return b.ret;
            const t = (risk - a.risk) / (b.risk - a.risk);
            return a.ret + t * (b.ret - a.ret);
        }
    }
    return null;
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
    
        const responseBox = document.getElementById("graph-response");
    const gapBox = document.getElementById("frontier-gap-message");

    function showMessage(text, isError = true) {
        responseBox.textContent = text;
        responseBox.style.color = isError ? "#ff5252" : "";
    }

    function renderFrontierGap(master, portfolio, total) {
        const risk = portfolio.portfolioStandardDeviation;
        const best = frontierReturnAtRisk(master.efData, master.mvpData, risk);

        console.log("Frontier gap check:", { risk, best, portfolioReturn: portfolio.portfolioReturn, total });

        const pct = x => (x * 100).toFixed(2) + "%";

        if (best === null) {
            gapBox.textContent = "Your portfolio's risk is outside the range covered by the efficient frontier, so no comparison is possible.";
            return;
        }

        const gap = best - portfolio.portfolioReturn;

        if (gap <= 0) {
            gapBox.textContent = "Your portfolio is already on (or above) the efficient frontier for its level of risk.";
            return;
        }

        const extra = gap * total;
        gapBox.textContent =
            `At the same risk (${pct(risk)}), an efficient portfolio is expected to return ${pct(best)} ` +
            `versus your ${pct(portfolio.portfolioReturn)}. That is about ` +
            `${extra.toLocaleString(undefined, { maximumFractionDigits: 0 })} more per period on your ` +
            `${total.toLocaleString()} invested. Based on historical estimates, not a guarantee.`;
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


            const total = amounts.reduce((a, b) => a + b, 0);
            const weights = amounts.map(a => a / total);

            showMessage("");
            gapBox.textContent = "";

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
    renderFrontierGap(master, portfolio, total);

    } catch (err) {
        console.error("Portfolio diagnostics error:", err);
        showMessage("Could not load data. Check that every ticker is valid. " + err.message);
    }
    });
});