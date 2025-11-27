# Delivery service

## Installation

### Prerequisites

- Python (3.12) / pip
- NodeJS (20.12.2) / npm
- Docker / docker-compose

### 1. Clone the repository
```bash
git clone https://github.com/Nemerzh/delivery_service_course_work.git delivery_service
cd delivery_service
```

### 2. Set up virtual environment (optional but recommended)
```bash
python -m venv venv
```
on unix based systems
```bash
source venv/bin/activate
```
on windows
```bash
venv\Scripts\activate
```

### 3. Install dependencies
```bash
pip install -r requirements.txt
```


### 4. Set up environment variables
Create a `.env` file from `.env.dist` and replace secret variables with your own values

### 5. Run docker-compose
run the command
```bash
docker-compose up -d
```


### 6. Run database migrations
```shell
python manage.py migrate
```


### 7. Run the server
```shell
python manage.py runserver
```
check console output
if everything is ok go to the frontend part
### 8. Setup frontend
```shell
cd frontend
npm i
npm run dev
```




