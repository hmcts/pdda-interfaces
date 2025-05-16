SET client_encoding TO 'UTF8';


-- Create a results logging table (if it doesn't exist)
CREATE TABLE IF NOT EXISTS pdda.pdda_hk_results (
        hk_result_id SERIAL PRIMARY KEY,
        job_name VARCHAR(255),
        job_start TIMESTAMP WITHOUT TIME ZONE,
        job_end TIMESTAMP WITHOUT TIME ZONE,
        status VARCHAR(20),
        job_text TEXT,
        error_message TEXT
);

