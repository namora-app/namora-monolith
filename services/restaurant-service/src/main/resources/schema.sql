CREATE EXTENSION IF NOT EXISTS postgis;
CREATE INDEX idx_restaurants_location_gist ON restaurants USING GIST(location);