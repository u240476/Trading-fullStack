const jwt = require("jsonwebtoken");

const JWT_SECRET = process.env.JWT_SECRET || "1a2b3c4d5e6f7g8h9i";

function authMiddleware(req, res, next) {
    const header = req.headers.authorization;

    if (!header) {
        return res.status(401).json({ error: "No token provided" });
    }

    const parts = header.split(" ");
    if (parts.length !== 2) {
        return res.status(401).json({ error: "Invalid authorization header" });
    }

    const token = parts[1];

    try {
        const decoded = jwt.verify(token, JWT_SECRET);
        req.user = decoded;
        return next();
    } catch (err) {
        return res.status(401).json({ error: "Invalid token" });
    }
}

module.exports = authMiddleware;