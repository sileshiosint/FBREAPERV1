#!/bin/bash

# Facebook OSINT Dashboard - System Startup Script
# This script starts all components of the system in the correct order

set -e

echo "🚀 Starting Facebook OSINT Dashboard System..."

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

# Check if Docker is running
if ! docker info > /dev/null 2>&1; then
    print_error "Docker is not running. Please start Docker and try again."
    exit 1
fi

# Check if required tools are installed
check_requirements() {
    print_status "Checking system requirements..."
    
    # Check Java
    if ! command -v java &> /dev/null; then
        print_error "Java is not installed. Please install Java 17+"
        exit 1
    fi
    
    # Check Node.js
    if ! command -v node &> /dev/null; then
        print_error "Node.js is not installed. Please install Node.js 18+"
        exit 1
    fi
    
    # Check Python
    if ! command -v python3 &> /dev/null; then
        print_error "Python 3 is not installed. Please install Python 3.8+"
        exit 1
    fi
    
    # Check Maven
    if ! command -v mvn &> /dev/null; then
        print_error "Maven is not installed. Please install Maven"
        exit 1
    fi
    
    print_success "All requirements are met"
}

# Start infrastructure services
start_infrastructure() {
    print_status "Starting infrastructure services (Neo4j, Kafka, Zookeeper)..."
    
    if [ -f "docker-compose.yml" ]; then
        docker-compose up -d
        print_success "Infrastructure services started"
        
        # Wait for services to be ready
        print_status "Waiting for services to be ready..."
        sleep 30
        
        # Check if services are running
        if docker ps | grep -q "fbreaper-neo4j" && docker ps | grep -q "fbreaper-kafka"; then
            print_success "Infrastructure services are ready"
        else
            print_error "Some infrastructure services failed to start"
            exit 1
        fi
    else
        print_error "docker-compose.yml not found"
        exit 1
    fi
}

# Start Java backend
start_backend() {
    print_status "Starting Java backend..."
    
    if [ -d "backend-java" ]; then
        cd backend-java
        
        # Check if Maven dependencies are installed
        if [ ! -d "target" ]; then
            print_status "Installing Maven dependencies..."
            mvn clean install -DskipTests
        fi
        
        # Start the backend in background
        print_status "Starting Spring Boot application..."
        mvn spring-boot:run > ../backend.log 2>&1 &
        BACKEND_PID=$!
        echo $BACKEND_PID > ../backend.pid
        
        cd ..
        
        # Wait for backend to start
        print_status "Waiting for backend to start..."
        sleep 30
        
        # Check if backend is responding
        if curl -s http://localhost:8080/api/health > /dev/null 2>&1; then
            print_success "Java backend is running on http://localhost:8080"
        else
            print_warning "Backend may still be starting up..."
        fi
    else
        print_error "backend-java directory not found"
        exit 1
    fi
}

# Start Python scraper
start_scraper() {
    print_status "Starting Python scraper..."
    
    if [ -d "python_scraper" ]; then
        cd python_scraper
        
        # Install Python dependencies if needed
        if [ ! -d "venv" ]; then
            print_status "Creating Python virtual environment..."
            python3 -m venv venv
        fi
        
        # Activate virtual environment and install dependencies
        source venv/bin/activate
        pip install -r requirements.txt
        
        # Start the scraper in background
        print_status "Starting Python scraper..."
        python main.py > ../scraper.log 2>&1 &
        SCRAPER_PID=$!
        echo $SCRAPER_PID > ../scraper.pid
        
        cd ..
        print_success "Python scraper is running"
    else
        print_error "python_scraper directory not found"
        exit 1
    fi
}

# Start React frontend
start_frontend() {
    print_status "Starting React frontend..."
    
    if [ -d "frontend" ]; then
        cd frontend
        
        # Install Node.js dependencies if needed
        if [ ! -d "node_modules" ]; then
            print_status "Installing Node.js dependencies..."
            npm install --no-audit --no-fund
        fi
        
        # Start the frontend in background
        print_status "Starting Vite development server..."
        npm run dev > ../frontend.log 2>&1 &
        FRONTEND_PID=$!
        echo $FRONTEND_PID > ../frontend.pid
        
        cd ..
        
        # Wait for frontend to start
        print_status "Waiting for frontend to start..."
        sleep 5
        
        print_success "Frontend is running on http://localhost:3000"
    else
        print_error "frontend directory not found"
        exit 1
    fi
}

# Main execution
main() {
    print_status "Starting Facebook OSINT Dashboard System..."
    
    # Check requirements
    check_requirements
    
    # Start services in order
    start_infrastructure
    start_backend
    start_scraper
    start_frontend
    
    echo ""
    print_success "🎉 System startup complete!"
    echo ""
    echo "📊 Services Status:"
    echo "  • Neo4j Database: http://localhost:7474"
    echo "  • Java Backend: http://localhost:8080"
    echo "  • React Frontend: http://localhost:3000"
    echo ""
    echo "📝 Logs:"
    echo "  • Backend: tail -f backend.log"
    echo "  • Scraper: tail -f scraper.log"
    echo "  • Frontend: tail -f frontend.log"
    echo ""
    echo "🛑 To stop the system, run: ./stop-system.sh"
    echo ""
}

# Handle script interruption
cleanup() {
    print_status "Shutting down system..."
    
    # Kill background processes
    if [ -f "backend.pid" ]; then
        kill $(cat backend.pid) 2>/dev/null || true
        rm backend.pid
    fi
    
    if [ -f "scraper.pid" ]; then
        kill $(cat scraper.pid) 2>/dev/null || true
        rm scraper.pid
    fi
    
    if [ -f "frontend.pid" ]; then
        kill $(cat frontend.pid) 2>/dev/null || true
        rm frontend.pid
    fi
    
    print_success "System shutdown complete"
}

# Set up signal handlers
trap cleanup SIGINT SIGTERM

# Run main function
main "$@"