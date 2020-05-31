DROP TABLE rating;
CREATE TABLE rating
(
    id      SERIAL PRIMARY KEY,
    ratingId VARCHAR(255),
    status  VARCHAR(255),
    rating   double PRECISION
);
