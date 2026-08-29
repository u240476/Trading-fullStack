const db = require("../config/db");

// CREATE USER
exports.createUser = (username, password, role) => {
    return new Promise((resolve, reject) => {
        const sql = "INSERT INTO accounts (username, password, role) VALUES (?, ?, ?)";
        db.run(sql, [username, password, role], function(err) {
            if (err) {
                return reject(err);
            }
            return resolve(this.lastID);
        });
    });
};

// FIND USER BY USERNAME
exports.findByUsername = (username) => {
    return new Promise((resolve, reject) => {
        const sql = "SELECT * FROM accounts WHERE username = ?";
        db.get(sql, [username], (err, row) => {
            if (err) {
                return reject(err);
            }
            return resolve(row);
        });
    });
};

// FIND USER BY ID
exports.findByAccountId = (id) => {
    return new Promise((resolve, reject) => {
        const sql = "SELECT * FROM accounts WHERE id = ?";
        db.get(sql, [id], (err, row) => {
            if (err) {
                return reject(err);
            }
            return resolve(row);
        });
    });
};

// DELETE USER
exports.deleteById = (id) => {
    return new Promise((resolve, reject) => {
        const sql = "DELETE FROM accounts WHERE id = ?";
        db.run(sql, [id], function(err) {
            if (err) {
                return reject(err);
            }
            return resolve(this.changes);
        });
    });
};






