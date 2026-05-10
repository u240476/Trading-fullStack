const express = require("express");
const cors = require("cors");

const app = express();

app.use(cors());
app.use(express.json());

const expectedReturnsRoute = require("./routes/ExpectedReturnsRoute");
const expectedPortfolioReturnsRoute = require("./routes/ExpectedPortfolioReturnsRoute");

app.use("/", expectedReturnsRoute);
app.use("/", expectedPortfolioReturnsRoute);

app.get("/", (req, res) => {
    res.send("Server is running");
});

app.listen(3010, () => {
    console.log("server is running on http://localhost:3010");
});