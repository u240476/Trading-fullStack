
document.addEventListener("DOMContentLoaded", () => {

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

    function parseCSV(input) {
        return input
            .split(/[\s,]+/)
            .map(x => x.trim())
            .filter(Boolean);
    }

    function clearInputs(ids) {
        ids.forEach(id => {
            document.getElementById(id).value = "";
        });
    }

    function addPoint(datasetIndex, x, y, label) {
        chart.data.datasets[datasetIndex].data.push({
            x,
            y,
            ticker: label
        });

        chart.update();
    }

    function validateInput(input) {
        return input && input.trim() !== "";
    }

    document.getElementById("clear-graph-button")
        .addEventListener("click", () => {

            chart.data.datasets.forEach(d => {
                d.data = [];
            });

            chart.update();
        });

    function setupGraphForm({
        formId,
        endpoint,
        datasetIndex,
        label,
        inputIds,
        buildParams,
        mapData,
        clearIds
    }) {

        document.getElementById(formId)
            .addEventListener("submit", async (e) => {

                e.preventDefault();

                const values = inputIds.map(id =>
                    document.getElementById(id).value.trim()
                );

                if (values.some(v => !validateInput(v))) {
                    alert("Please enter all required fields.");
                    return;
                }

                try {

                    const params = buildParams(values);

                    const res = await fetch(
                        `${endpoint}?${params.toString()}`
                    );

                    if (!res.ok) {
                        throw new Error(await res.text());
                    }

                    const data = await res.json();

                    const point = mapData(data, values);

                    addPoint(
                        datasetIndex,
                        point.x,
                        point.y,
                        point.label
                    );

                    clearInputs(clearIds);

                } catch (err) {
                    console.error(err);
                }
            });
    }

    setupGraphForm({
        formId: "graph-form",

        endpoint: "http://localhost:3010/api/graph-data",

        datasetIndex: 0,

        label: "Stock",

        inputIds: ["graph-ticker-input"],

        buildParams: ([ticker]) =>
            new URLSearchParams({
                ticker: ticker.toUpperCase()
            }),

        mapData: (data, [ticker]) => ({
            x: data.standardDeviation * 100,
            y: data.expectedReturn * 100,
            label: ticker.toUpperCase()
        }),

        clearIds: ["graph-ticker-input"]
    });

    setupGraphForm({
        formId: "graph-mvp-form",

        endpoint: "http://localhost:3010/api/graph-mvp-data",

        datasetIndex: 1,

        label: "MVP",

        inputIds: ["graph-mvp-tickers-input"],

        buildParams: ([tickers]) =>
            new URLSearchParams({
                tickers: parseCSV(tickers).join(",")
            }),

        mapData: (data) => ({
            x: data.portfolioStandardDeviation * 100,
            y: data.portfolioReturn * 100,
            label: "MVP"
        }),

        clearIds: ["graph-mvp-tickers-input"]
    });


    setupGraphForm({
        formId: "graph-tp-form",

        endpoint: "http://localhost:3010/api/graph-tp-data",

        datasetIndex: 2,

        label: "TP",

        inputIds: ["graph-tp-tickers-input"],

        buildParams: ([tickers]) =>
            new URLSearchParams({
                tickers: parseCSV(tickers).join(",")
            }),

        mapData: (data) => ({
            x: data.portfolioStandardDeviation * 100,
            y: data.portfolioReturn * 100,
            label: "TP"
        }),

        clearIds: ["graph-tp-tickers-input"]
    });


    setupGraphForm({
        formId: "graph-portfolio-form",

        endpoint: "http://localhost:3010/api/graph-portfolio-data",

        datasetIndex: 3,

        label: "Your Portfolio",

        inputIds: [
            "graph-portfolio-tickers-input",
            "graph-portfolio-proportions-input"
        ],

        buildParams: ([tickers, proportions]) =>
            new URLSearchParams({
                tickers: parseCSV(tickers).join(","),
                proportions: parseCSV(proportions).join(",")
            }),

        mapData: (data) => ({
            x: data.portfolioStandardDeviation * 100,
            y: data.portfolioReturn * 100,
            label: "Your Portfolio"
        }),

        clearIds: [
            "graph-portfolio-tickers-input",
            "graph-portfolio-proportions-input"
        ]
    });

});