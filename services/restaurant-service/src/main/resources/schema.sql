CREATE EXTENSION IF NOT EXISTS postgis;
CREATE INDEX IF NOT EXISTS idx_restaurants_location_gist ON restaurants USING GIST(location);