#!/bin/bash

# Facebook OSINT Dashboard - System Stop Script
# This script stops all components of the system gracefully

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

# Function to print colored output
print_status() {
    echo -e "${BLUE}[INFO]${NC} $1"
}

print_success() {
    echo -e "${GREEN}[SUCCESS]${NC} $1"
}

print_warning() {
    echo -e "${YELLOW}[WARNING]${NC} $1"
}

print_error() {
    echo -e "${RED}[ERROR]${NC} $1"
}

echo "🛑 Stopping Facebook OSINT Dashboard System..."

# Stop React frontend
if [ -f "frontend.pid" ]; then
    FRONTEND_PID=$(cat frontend.pid)
    if kill -0 $FRONTEND_PID 2>/dev/null; then
        print_status "Stopping React frontend (PID: $FRONTEND_PID)..."
        kill $FRONTEND_PID
        sleep 5
        if kill -0 $FRONTEND_PID 2>/dev/null; then
            print_warning "Frontend didn't stop gracefully, force killing..."
            kill -9 $FRONTEND_PID
        fi
        print_success "React frontend stopped"
    else
        print_warning "Frontend process not running"
    fi
    rm -f frontend.pid
else
    print_warning "Frontend PID file not found"
fi

# Stop Python scraper
if [ -f "scraper.pid" ]; then
    SCRAPER_PID=$(cat scraper.pid)
    if kill -0 $SCRAPER_PID 2>/dev/null; then
        print_status "Stopping Python scraper (PID: $SCRAPER_PID)..."
        kill $SCRAPER_PID
        sleep 5
        if kill -0 $SCRAPER_PID 2>/dev/null; then
            print_warning "Scraper didn't stop gracefully, force killing..."
            kill -9 $SCRAPER_PID
        fi
        print_success "Python scraper stopped"
    else
        print_warning "Scraper process not running"
    fi
    rm -f scraper.pid
else
    print_warning "Scraper PID file not found"
fi

# Stop Java backend
if [ -f "backend.pid" ]; then
    BACKEND_PID=$(cat backend.pid)
    if kill -0 $BACKEND_PID 2>/dev/null; then
        print_status "Stopping Java backend (PID: $BACKEND_PID)..."
        kill $BACKEND_PID
        sleep 10
        if kill -0 $BACKEND_PID 2>/dev/null; then
            print_warning "Backend didn't stop gracefully, force killing..."
            kill -9 $BACKEND_PID
        fi
        print_success "Java backend stopped"
    else
        print_warning "Backend process not running"
    fi
    rm -f backend.pid
else
    print_warning "Backend PID file not found"
fi

# Stop infrastructure services
print_status "Stopping infrastructure services (Neo4j, Kafka, Zookeeper)..."
if [ -f "docker-compose.yml" ]; then
    docker-compose down
    print_success "Infrastructure services stopped"
else
    print_warning "docker-compose.yml not found"
fi

# Clean up any remaining processes
print_status "Cleaning up any remaining processes..."

# Kill any remaining Node.js processes (Vite)
pkill -f "vite" 2>/dev/null || true

# Kill any remaining Python processes (scraper)
pkill -f "python.*main.py" 2>/dev/null || true

# Kill any remaining Java processes (Spring Boot)
pkill -f "spring-boot" 2>/dev/null || true

print_success "🎉 System shutdown complete!"

echo ""
echo "📊 Services Status:"
echo "  • All services have been stopped"
echo ""
echo "🧹 To clean up completely, you can also run:"
echo "  • docker system prune -f (removes unused Docker resources)"
echo "  • rm -f *.log *.pid (removes log and PID files)"
echo ""