# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

This is an academic conference management platform (huiwutong) with a documentation-first architecture. The project is currently in the planning phase - code has not been implemented yet, but comprehensive requirements and technical architecture documentation exists.

The system serves four user roles:
- **投稿讲者 (Speakers)**: Submit papers, track submission status, receive email notifications
- **审稿人 (Reviewers)**: Review submissions, provide feedback
- **参会者 (Attendees)**: Register for conference, purchase tickets, receive entry credentials
- **管理员 (Admin)**: Manage all aspects of the conference

## Technology Stack

### Frontend (Two Projects)
- **Framework**: Vue 3 + TypeScript
- **Build Tool**: Vite
- **UI Library**: Element Plus
- **State Management**: Pinia
- **Routing**: Vue Router
- **HTTP Client**: Axios
- **Styling**: SCSS with standardized design tokens

### Backend
- **Framework**: Java 17 + Spring Boot
- **Build Tool**: Maven
- **ORM**: MyBatis Plus
- **Database**: MySQL 8.0
- **Cache**: Redis
- **Authentication**: JWT
- **Validation**: Jakarta Bean Validation

## Project Structure

```
huiwutong/
├── frontend/
│   ├── portal/           # Portal for speakers/attendees/reviewers (empty - to be implemented)
│   └── admin/            # Admin panel (empty - to be implemented)
├── backend/              # Spring Boot API (empty - to be implemented)
├── docs/
│   ├── 需求分析/
│   │   └── 需求文档.md  # Comprehensive functional requirements
│   └── 技术实现/
│       ├── 技术架构.md  # Complete technical architecture with code examples
│       ├── 数据库设计.md  # Complete database schema with init.sql
│       ├── 模块分析/  # Detailed module analysis per feature
│       └── 前端设计/  # Frontend design specs for portal and admin
└── 需求文档/             # Additional requirements documentation
```

## Expected Build/Run Commands

Since the project is in planning phase, these commands will be used once implementation begins:

### Backend (Maven)
```bash
cd backend
mvn clean install           # Build the project
mvn spring-boot:run         # Run development server (port 8080)
mvn test                    # Run tests
```

### Frontend - Portal
```bash
cd frontend/portal
npm install                 # Install dependencies
npm run dev                 # Run dev server
npm run build               # Build for production
npm run lint                # Run ESLint
npm run type-check          # TypeScript type checking
```

### Frontend - Admin
```bash
cd frontend/admin
npm install
npm run dev                 # Run dev server
npm run build               # Build for production
```

### Docker Deployment
```bash
docker-compose up -d        # Start all services
docker-compose down          # Stop all services
docker-compose logs backend  # View backend logs
```

## Architecture Overview

### Frontend Architecture

**Portal Project Structure** (when implemented):
- `src/api/` - API interface layer with type definitions
- `src/components/` - Reusable Vue components
- `src/composables/` - Composition API functions (useAuth, usePagination, useFileUpload)
- `src/router/` - Vue Router configuration with guards for role-based access
- `src/stores/` - Pinia stores (auth, user, submission, order)
- `src/utils/` - Utilities including Axios wrapper with request/response interceptors
- `src/views/` - Page components organized by feature

**Admin Project Structure** (when implemented):
- Similar to Portal, with additional:
  - `src/views/dashboard/` - Statistics and charts
  - `src/views/user/` - User management
  - `src/views/ticket/` - Ticket management

### Backend Architecture

**Layered Architecture**: Controller → Service → Mapper → Database

**Key Packages** (when implemented):
- `common/annotation/` - `@RequireLogin`, `@RequireRole` for access control
- `common/exception/` - `GlobalExceptionHandler`, `BusinessException`
- `common/util/` - `JwtUtil` for token generation/validation
- `common/filter/` - `JwtAuthenticationFilter` for token extraction
- `common/vo/` - `Result<T>` (unified response), `PageResult<T>` (pagination)

**Controller Layer**: Receives requests, validates with `@Valid`, calls Service, returns `Result`
**Service Layer**: Business logic, uses Lombok, throws `BusinessException`
**Mapper Layer**: MyBatis Plus database operations

## API Response Format

### Unified Response
```json
{
  "code": 200,
  "message": "操作成功",
  "data": {},
  "timestamp": 1700000000000
}
```

### Pagination Response
```json
{
  "code": 200,
  "message": "操作成功",
  "data": {
    "list": [],
    "total": 100,
    "page": 1,
    "pageSize": 10
  },
  "timestamp": 1700000000000
}
```

## Error Handling

Frontend Axios interceptors handle:
- Code 401: Redirect to login, clear token
- Code 200+: Success
- Other codes: Display error message

Backend `GlobalExceptionHandler` handles:
- `BusinessException`: Returns business error with custom code/message
- `MethodArgumentNotValidException`: Returns field validation errors
- Generic exceptions: Returns 500 with "系统繁忙，请稍后重试"

## Authentication Flow

1. Login: `POST /api/auth/login` → Returns `{ token, userInfo }`
2. Token storage: Frontend stores in localStorage via `storage` utility
3. Request: Axios adds `Authorization: Bearer {token}` header
4. Backend: `JwtAuthenticationFilter` extracts and validates token, sets request attributes
5. Authorization: Use `@RequireLogin` or `@RequireRole({ "admin", "speaker" })` annotations

## Database Schema

**8 Core Tables**:
- `dictionary` - Dictionary definitions (dict_code, dict_name)
- `dictionary_item` - Dictionary items with enum values stored as item_value (TINYINT in business tables)
- `users` - User management with role-based access (1=speaker,2=reviewer,3=attendee,4=admin)
- `submissions` - Paper submissions with status tracking (1=pending,2=reviewing,3=accepted,4=rejected)
- `reviews` - Review records with feedback and scores
- `tickets` - Conference ticket types with inventory
- `orders` - Registration orders with payment status (1=unpaid,2=paid,3=cancelled,4=refunded)
- `credentials` - Entry credentials with QR codes

**Key Design Decision**: All enum values (role, status, etc.) are maintained via `dictionary` and `dictionary_item` tables. Business tables store TINYINT values corresponding to `item_value` from dictionary items.

## Development Standards

### Frontend Naming Conventions
- Components: PascalCase (`UserProfile.vue`)
- Pages: PascalCase (`Login.vue`)
- Utils: camelCase (`formatDate.ts`)
- Constants: UPPER_SNAKE_CASE (`API_BASE_URL`)
- CSS classes: kebab-case (`user-profile`)

### Backend Naming Conventions
- Classes: PascalCase (`UserServiceImpl`)
- Methods: camelCase (`getUserById`)
- Constants: UPPER_SNAKE_CASE (`MAX_RETRY_COUNT`)
- Database tables: snake_case (`user_profiles`)

### Git Commit Convention
```
feat: 新功能
fix: 修复bug
docs: 文档更新
style: 代码格式调整
refactor: 代码重构
test: 测试相关
chore: 构建/工具变动
```

## Key Integration Points

- JWT token expiration: 24 hours (configurable)
- File upload max size: 10MB
- Backend port: 8080 (dev), configurable via `SERVER_PORT`
- Portal port: 3000, Admin port: 3001 (when using dev servers)
- MySQL port: 3306, Redis port: 6379 (Docker defaults)

## Important Implementation Notes

1. **TypeScript Strict Mode**: Enabled for all frontend projects
2. **Code Style**: ESLint + Prettier required for frontend; Alibaba Java Handbook for backend
3. **Role-Based Access**: Frontend route guards check both `isLogin` and `userRole`
4. **API Type Safety**: All API calls must have TypeScript interface definitions
5. **Password Encryption**: Use BCrypt (backend) - never store plaintext passwords
6. **SQL Injection Prevention**: MyBatis Plus parameter binding handles this automatically

## Documentation References

- `docs/需求分析/需求文档.md` - Full functional requirements, API endpoints, user flows
- `docs/技术实现/技术架构.md` - Complete project structures, code examples, configuration files
- `docs/技术实现/数据库设计.md` - Database schema, relationships, initialization script
- `docs/技术实现/模块分析/` - Detailed analysis for each business module
- `docs/技术实现/前端设计/portal设计.md` - Portal frontend design with component examples
- `docs/技术实现/前端设计/admin设计.md` - Admin frontend design with layout specs

## Language Note

This project uses Chinese as the primary language for:
- User-facing text (UI labels, messages, emails)
- Database comments
- API error messages
- Documentation

Code comments and technical variable names should use English.
