# Elasticsearch Courses Project

This project is a Spring Boot application that integrates with Elasticsearch to manage and search course data. It supports advanced search queries including fuzzy search and autocomplete suggestions.

## Prerequisites

* Docker and Docker Compose installed
* Java 17+
* Maven 3.8+

## 1. Launch Elasticsearch

> **Note:** All commands must be run inside the project folder because `docker-compose.yml` is located here.

```bash
# Navigate to the project folder
cd path/to/your/project

# Start Elasticsearch container
docker-compose up -d
```

Verify Elasticsearch is running:

```bash
curl http://localhost:9200
```

**Response:**
```json
{
    "name" : "elasticsearch-1",
    "cluster_name" : "docker-cluster",
    "cluster_uuid" : "xxxxxxxxxx",
    "version" : {
    "number" : "8.15.0",
    "build_flavor" : "default",
    "build_type" : "docker",
    "build_hash" : "xxxxxxxxxx",
    "build_date" : "2025-08-31T00:00:00.000Z",
    "build_snapshot" : false,
    "lucene_version" : "9.x",
    "minimum_wire_compatibility_version" : "7.17.0",
    "minimum_index_compatibility_version" : "7.0.0"
    },
    "tagline" : "You Know, for Search"
}
```

## 2. Build and Run Spring Boot Application

The project uses Maven Wrapper, so you don’t need Maven installed.

# Build the application
./mvnw clean install -DskipTests  //you can manually run the test class to see

# Run the application
./mvnw spring-boot:run

The application will start on `http://localhost:8080` by default.

## 3. Populate Index with Sample Data

The application automatically loads sample course data from `sample-courses.json` on startup using the `DataLoader` component.

You should see output like:

```
Loaded X courses into Elasticsearch
```

## 4. API Endpoints

### Search Courses

**Endpoint:** `GET /api/search`

**Query Parameters:**

* `q` (optional): Keyword search
* `category` (optional)
* `type` (optional)
* `minAge` / `maxAge` (optional)
* `minPrice` / `maxPrice` (optional)
* `startDate` (optional, format `yyyy-MM-dd'T'HH:mm:ssX`)
* `sort` (optional, `priceAsc`, `priceDesc`, default `nextSessionDate`)
* `page` / `size` (optional, default 0/10)

**Example curl:**

```bash
curl "http://localhost:8080/api/search?q=algebra&category=Math&page=0&size=5"
```

**Response:**

```json
{
  "total": 2,
  "courses": [
    {
      "id": "1",
      "title": "Introduction to Algebra",
      "category": "Math",
      "price": 120.5,
      "nextSessionDate": "2025-09-01T10:00:00Z"
    },
    {
      "id": "8",
      "title": "Advanced Algebra",
      "category": "Math",
      "price": 140.0,
      "nextSessionDate": "2025-09-15T10:00:00Z"
    }
  ]
}
```

### Autocomplete Suggestions

**Endpoint:** `GET /api/search/suggest`

**Query Parameters:**

* `q`: Partial course title

**Example curl:**

```bash
curl "http://localhost:8080/api/search/suggest?q=Intro"
```

**Sample Response:**

```json
[
  "Introduction to Algebra",
  "Introduction to French greetings, vocabulary, and simple conversation"
]
```

### Fuzzy Search

Fuzzy search is automatically applied on `q` parameter in `/api/search`.

**Example curl:**

```bash
curl "http://localhost:8080/api/search?q=algibra"
```

**Sample Response:**

```json
{
  "total": 2,
  "courses": [
    {
      "id": "1",
      "title": "Introduction to Algebra",
      "category": "Math",
      "price": 120.5
    },
    {
      "id": "8",
      "title": "Advanced Algebra",
      "category": "Math",
      "price": 140.0
    }
  ]
}
```

---
