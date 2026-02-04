#!/bin/bash

echo "🐾 AdoptMe - Week 1 Setup Script"
echo "================================="
echo ""

# Check Java installation
echo "Checking Java installation..."
if command -v java &> /dev/null; then
    JAVA_VERSION=$(java -version 2>&1 | awk -F '"' '/version/ {print $2}')
    echo "✅ Java found: $JAVA_VERSION"
else
    echo "❌ Java not found. Please install Java 17 or higher."
    exit 1
fi

# Check PostgreSQL
echo ""
echo "Checking PostgreSQL..."
if command -v psql &> /dev/null; then
    echo "✅ PostgreSQL found"
else
    echo "⚠️  PostgreSQL CLI not found. Make sure PostgreSQL is installed."
fi

# Check Docker (optional)
echo ""
echo "Checking Docker (optional)..."
if command -v docker &> /dev/null; then
    echo "✅ Docker found"
    echo ""
    read -p "Do you want to use Docker for PostgreSQL? (y/n): " use_docker
    if [ "$use_docker" = "y" ]; then
        echo "Starting PostgreSQL with Docker..."
        docker-compose up -d
        echo "✅ PostgreSQL started on port 5432"
    fi
else
    echo "⚠️  Docker not found. Will use local PostgreSQL."
fi

# Create database (if using local PostgreSQL)
if [ "$use_docker" != "y" ]; then
    echo ""
    echo "Setting up local PostgreSQL database..."
    echo "Please run this SQL command manually:"
    echo "CREATE DATABASE adoptme_db;"
    echo ""
fi

# Run the application
echo ""
echo "Starting Spring Boot application..."
echo ""
./mvnw clean install
./mvnw spring-boot:run

echo ""
echo "✅ Setup complete!"
echo "Application should be running on http://localhost:8080"
