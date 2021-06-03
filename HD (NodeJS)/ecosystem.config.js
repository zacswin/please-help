module.exports = {
	apps: [
		{
			name: "please-help",
			script: "./dist/main.js",
			watch: true,
			exec_mode: "cluster",
			instances: "1",
			env: {
				"PORT": 90,
				"NODE_ENV": "production",
				"DB_USERNAME": "postgres",
				"DB_PASSWORD": "postgres",
				"DB_DATABASE_NAME": "please_help",
				"JWT_SECRET": "secret"
			}
		}
	]
}