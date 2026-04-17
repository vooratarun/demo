# AGENTS.md - Demo Spring Boot Application

## Architecture Overview

This is a **multi-layer Spring Boot 3.5.6 application** (Java 17) with:
- **API Layer**: RESTful controllers with OpenAPI/Swagger documentation
- **Service Layer**: Business logic (service implementations)
- **Data Layer**: Dual persistence - JPA/Hibernate for MySQL + jOOQ for type-safe SQL queries
- **Security**: JWT-based stateless authentication with Spring Security
- **Integrations**: AWS (Secrets Manager, DynamoDB, SQS), Kafka, Redis, MongoDB, Flyway migrations

**Key Pattern**: The app uses a mix of traditional Spring Data JPA repositories AND jOOQ for complex queries (see `CustomerJooqRepository` - currently commented but shows the pattern).

## Build & Runtime

- **Build**: Maven (`./mvnw clean package`)
- **Database**: MySQL at `localhost:3306/demo` (root/rootpass) with auto-schema via `spring.jpa.hibernate.ddl-auto=update`
- **Port**: 6060
- **Profiles**: `dev` (H2) and `prod` (MySQL) - configured in `DatabaseConfig.java`
- **Key Startup Checks**: 
  - Redis at `:6379` (for `@Cacheable`)
  - MongoDB at `localhost:27017/appdb` (for document storage)
  - Kafka topics: `demo-topic`, `demo-notification-topic` (optional, not required for core tests)
  - AWS LocalStack at `:4566` (for Secrets Manager/DynamoDB/SQS testing)

## Core Workflows

### Adding a New Endpoint
1. Create controller in `/controllers/` with `@RestController`, `@RequestMapping`
2. Inject service layer (prefer constructor injection, see `ApiController.java`)
3. Service calls repository layer
4. Exception handling: `GlobalExceptionHandler.java` catches exceptions → `ErrorResponse.java`
5. For API docs: Add `@Tag` and `@Operation` annotations (see `ProductController.java`)

### Entity & Repository Pattern
- **Entities**: In `/entity/` (e.g., `Student.java`, `Product.java`) - use Lombok `@Data`
- **Repositories**: In `/repo/`, extend `JpaRepository<Entity, ID>` (e.g., `StudentServiceImpl` uses repository)
- **Naming**: Service layer typically named `*ServiceImpl` or `*Service` (mixed convention in codebase)

### Authentication Flow
1. `/api/auth/register` and `/api/auth/login` are **public** endpoints (see `SecurityConfig.java`)
2. JWT generation in `JwtTokenUtil.generateToken()` - 10-hour expiry hardcoded
3. `JwtRequestFilter` validates token on every request (except public routes)
4. User roles via `Authority` entity → checked in security config
5. ⚠️ **Security Note**: `SECRET_KEY` is hardcoded; should use environment variables in production

## Key Configurations & Naming

| Component | Location | Pattern |
|-----------|----------|---------|
| Security | `config/SecurityConfig.java` | Stateless (CSRF disabled), JWT filter before `UsernamePasswordAuthenticationFilter` |
| Async Tasks | `config/AsyncConfig.java` + `@Async` | ThreadPool: core=5, max=10, queue=100 |
| Caching | `config/RedisConfig.java` + `@Cacheable` | Redis at `:6379`; use `@CacheEvict` to invalidate |
| DB Queries | `config/JooqConfig.java` | jOOQ DSLContext configured for MySQL dialect |
| S3 File Storage | `config/S3Config.java` + `services/S3Service.java` | Upload, download, list, delete files; bucket auto-create on init |
| KMS Encryption | `config/KmsConfig.java` + `services/KmsService.java` | Encrypt/decrypt data, manage keys, envelope encryption |
| GraphQL API | `config/GraphQLConfig.java` + `graphql/TodoQueryResolver.java`, `TodoMutationResolver.java` | Query & Mutation resolvers for Todo operations; schema in `/graphql/schema.graphqls` |
| Interceptors | `interceptor/PerformanceInterceptor.java` | Logs request duration to stdout |
| Filters | `filter/FirstFilter.java`, `SecondFilter.java` | @Order(1), @Order(2) - run in order before controllers |

## External Service Integration Points

- **AWS Secrets Manager** (`config/AwsSecretsManagerConfig.java`): Loads `my-secret` at startup via `AwsSecretsEnvironmentPostProcessor`
- **DynamoDB** (`config/DynamoDbConfig.java`): `DynamoTodoService` handles CRUD on `todos` table
- **SQS** (`config/SqsConfig.java`): Polled via cron `0 */1 * * * *` (every minute)
- **S3** (`config/S3Config.java` + `services/S3Service.java`): File upload/download/listing via `/api/s3/*` endpoints
- **KMS** (`config/KmsConfig.java` + `services/KmsService.java`): Encrypt/decrypt data, key management via `/api/kms/*` endpoints
- **Kafka** (`kafka/`): Producers/consumers for `demo-topic`; idempotent producer enabled
- **Email/SMS**: `EmailService`, `SMSNotificationService` - abstract `NotificationService` pattern
- **GraphQL** (`graphql/`): Query/Mutation resolvers for Todo operations; schema-first approach with Spring GraphQL

## GraphQL Todo API

## GraphQL Todo API

### GraphQL Endpoints
- **GraphQL API**: `POST /graphql` - Execute queries and mutations
- **GraphiQL UI**: `GET /graphiql` - Interactive GraphQL IDE for testing
- **Subscriptions**: `WS /graphql` - WebSocket for real-time updates (configured but not active)

### GraphQL Schema
The schema is defined in `/src/main/resources/graphql/schema.graphqls`:
```graphql
type Query {
  todos: [Todo!]!
  todo(id: ID!): Todo
}

type Mutation {
  createTodo(input: CreateTodoInput!): Todo!
  updateTodo(id: ID!, input: UpdateTodoInput!): Todo
  deleteTodo(id: ID!): Boolean!
  completeTodo(id: ID!): Todo
}

type Todo {
  id: ID!
  title: String!
  description: String
  completed: Boolean!
}
```

### Sample Queries & Mutations

**Get All Todos**:
```graphql
query {
  todos {
    id
    title
    description
    completed
  }
}
```

**Create Todo**:
```graphql
mutation {
  createTodo(input: {
    title: "Learn GraphQL"
    description: "Understand GraphQL basics"
  }) {
    id
    title
    completed
  }
}
```

**Update Todo**:
```graphql
mutation {
  updateTodo(id: "todo-id", input: {
    title: "Updated title"
    completed: true
  }) {
    id
    title
    completed
  }
}
```

### GraphQL Project Structure
- **Schema**: `/src/main/resources/graphql/schema.graphqls` - Type definitions
- **Resolvers**: 
  - `/src/main/java/.../graphql/TodoQueryResolver.java` - @QueryMapping methods
  - `/src/main/java/.../graphql/TodoMutationResolver.java` - @MutationMapping methods
- **DTOs**: 
  - `/src/main/java/.../dto/CreateTodoInput.java`
  - `/src/main/java/.../dto/UpdateTodoInput.java`
- **Configuration**: `/src/main/java/.../config/GraphQLConfig.java`

### cURL Examples

**Get all todos**:
```bash
curl -X POST http://localhost:6060/graphql \
  -H "Content-Type: application/json" \
  -d '{"query":"query { todos { id title description completed } }"}'
```

**Create todo**:
```bash
curl -X POST http://localhost:6060/graphql \
  -H "Content-Type: application/json" \
  -d '{"query":"mutation { createTodo(input: { title: \"New Todo\" description: \"Test\" }) { id title completed } }"}'
```

### Key Points
- GraphQL endpoint is **public** (no authentication required)
- Resolvers delegate to `TodoService` which handles MongoDB persistence
- Both GraphQL and REST `/api/todos/*` endpoints operate on the same data
- GraphiQL UI at `/graphiql` provides interactive schema exploration and testing
- See `GRAPHQL_TESTING_GUIDE.md` for comprehensive examples and usage patterns



### S3 Service Methods
The `S3Service` provides core file operations:
- **uploadFile(key, file)**: Upload multipart file; returns S3 URI
- **downloadFile(key)**: Retrieve file as byte array
- **deleteFile(key)**: Remove file from bucket
- **listFiles(prefix)**: List all objects matching prefix
- **objectExists(key)**: Check file presence
- **createBucketIfNotExists()**: Initialize bucket (call on startup if needed)

### S3 API Endpoints
All endpoints in `S3Controller`:
- `POST /api/s3/upload` - Upload file (multipart form-data); optional custom key
- `GET /api/s3/download/{key}` - Download file by key
- `DELETE /api/s3/{key}` - Delete file
- `GET /api/s3/list?prefix=...` - List files (optional prefix filter)
- `GET /api/s3/exists/{key}` - Check if file exists
- `GET /api/s3/presigned-url/{key}` - Get public/signed URL
- `POST /api/s3/init-bucket` - Create bucket if missing

### Configuration
S3 properties in `application.properties`:
```properties
aws.s3.region=us-east-1
aws.s3.endpoint=http://localhost:4566  # LocalStack for dev
aws.s3.access-key=test
aws.s3.secret-key=test
aws.s3.bucket-name=demo-bucket
```

### Injecting S3Service
```java
@RestController
public class FileController {
    private final S3Service s3Service;
    
    public FileController(S3Service s3Service) {
        this.s3Service = s3Service;
    }
    
    public void uploadExample(MultipartFile file) {
        String s3Url = s3Service.uploadFile("my-file.txt", file);
    }
}
```

### LocalStack Testing
For local dev, configure LocalStack S3 mock at `:4566`:
```bash
docker-compose up localstack  # Ensure S3 is available
# Then run app - bucket auto-creates on first upload

## KMS Encryption Usage

### KMS Service Methods
The `KmsService` provides comprehensive encryption operations:
- **encrypt(plaintext)**: Encrypt data using KMS → returns Base64 ciphertext
- **decrypt(encryptedBase64)**: Decrypt Base64 ciphertext → returns plaintext
- **generateDataKey()**: Generate data key for envelope encryption
- **createKey(description)**: Create new KMS key → returns key ID
- **listKeys()**: List all KMS keys in account
- **describeKey(keyId)**: Get detailed key information
- **enableKey(keyId)**: Enable a disabled key
- **disableKey(keyId)**: Disable a key
- **scheduleKeyDeletion(keyId, days)**: Schedule key deletion (7-30 day window)

### KMS API Endpoints
All endpoints in `KmsController`:
- `POST /api/kms/encrypt` - Encrypt plaintext data
- `POST /api/kms/decrypt` - Decrypt ciphertext data
- `POST /api/kms/generate-data-key` - Generate data key for envelope encryption
- `POST /api/kms/create-key` - Create new KMS key
- `GET /api/kms/keys` - List all KMS keys
- `GET /api/kms/keys/{keyId}` - Get key information
- `POST /api/kms/keys/{keyId}/enable` - Enable a key
- `POST /api/kms/keys/{keyId}/disable` - Disable a key
- `DELETE /api/kms/keys/{keyId}` - Schedule key deletion
- `POST /api/kms/test-round-trip` - Test encrypt/decrypt round trip

### Configuration
KMS properties in `application.properties`:
```properties
aws.kms.region=us-east-1
aws.kms.endpoint=http://localhost:4566  # LocalStack for dev
aws.kms.access-key=test
aws.kms.secret-key=test
aws.kms.key-id=alias/demo-key
```

### Injecting KmsService
```java
@RestController
public class SecureController {
    private final KmsService kmsService;
    
    public SecureController(KmsService kmsService) {
        this.kmsService = kmsService;
    }
    
    @PostMapping("/encrypt-data")
    public String encryptSensitiveData(@RequestBody String data) {
        return kmsService.encrypt(data);
    }
}
```

### LocalStack Testing
For local dev, configure LocalStack KMS mock at `:4566`:
```bash
docker-compose up localstack  # Ensure KMS is available
# Then run app - create keys via API endpoints
```

### Sample API Usage

#### Encrypt Data
```bash
curl -X POST http://localhost:6060/api/kms/encrypt \
  -H "Content-Type: application/json" \
  -d '{"plaintext": "Hello, World!"}'
```

#### Decrypt Data
```bash
curl -X POST http://localhost:6060/api/kms/decrypt \
  -H "Content-Type: application/json" \
  -d '{"encryptedData": "BASE64_ENCRYPTED_DATA"}'
```

#### Test Round Trip
```bash
curl -X POST http://localhost:6060/api/kms/test-round-trip \
  -H "Content-Type: application/json" \
  -d '{"testData": "Test message"}'
```

#### Create Key
```bash
curl -X POST http://localhost:6060/api/kms/create-key \
  -H "Content-Type: application/json" \
  -d '{"description": "My test key"}'
```

#### List Keys
```bash
curl http://localhost:6060/api/kms/keys
```

## Common Patterns & Conventions

### Service Implementation
```java
// Use constructor injection, not @Autowired
@Service
public class StudentServiceImpl {
    private final StudentRepository repository;
    public StudentServiceImpl(StudentRepository repository) { this.repository = repository; }
}
```

### DTO vs Entity
- DTOs in `/dto/` (e.g., `JwtRequest`, `JwtResponse`)
- Entities in `/entity/` - use for persistence, DTOs for API contracts

### Exception Handling
- Custom exceptions in `/exception/` (e.g., `UserNotFoundException`)
- Caught by `GlobalExceptionHandler` → returns `ErrorResponse` with HTTP status
- Controllers throw, don't catch

### Dependency Injection
- **Preferred**: Constructor injection (type-safe, testable)
- **Also Used**: `@Autowired` fields (legacy, seen in multiple controllers)
- **Pattern**: Both styles coexist; prefer constructor for new code

## Important File Locations

- **Main App**: `DemoApplication.java` - enables `@EnableWebSecurity`, `@EnableCaching`, `@EnableAsync`, `@EnableScheduling`
- **API Docs**: `config/OpenApiConfig.java` - defines Bearer JWT scheme for Swagger
- **Migrations**: `resources/db/migration/` - V2__ migration adds phone number to customers (Flyway)
- **Profiles**: `resources/application-dev.properties`, `application-prod.properties` (profiles not active by default)
- **SSL Certs** (for Kafka): `resources/ssl/` - ca.pem, service.cert, service.key

## Gotchas & Debugging

1. **JWT Token Expiry**: Hardcoded to 10 hours in `JwtTokenUtil.createToken()` - change if needed
2. **MySQL Connection**: Ensures `useSSL=false&allowPublicKeyRetrieval=true` for local dev
3. **jOOQ Code Generation**: Commented-out example in `CustomerJooqRepository.java` - jOOQ generator not configured; use for reference only
4. **Flyway Disabled**: `spring.flyway.enabled=false` in properties - migrations not auto-run (manual or use dev profile)
5. **Filter/Interceptor Overlap**: Both exist; filters run pre-Spring, interceptors run in DispatcherServlet
6. **MongoDB/Redis Optional**: Core app works without; check service implementations for fallback behavior

## Quick Command Reference

```bash
# Build
./mvnw clean package

# Run (on port 6060)
./mvnw spring-boot:run

# Test
./mvnw test

# Skip tests build
./mvnw clean package -DskipTests
```

## Testing Notes

- Basic context test in `DemoApplicationTests.java` (contextLoads)
- Integration tests require MySQL, Redis, MongoDB running
- Unit tests: Mock repositories using Mockito; no @SpringBootTest needed
- Consider profile-based test properties (e.g., `application-test.properties`)
