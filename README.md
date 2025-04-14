# College Management System

## Overview

The College Management System is a comprehensive web-based application designed to streamline and automate various administrative processes within educational institutions. Built with Java Spring Boot and modern web technologies, this system provides role-based access control for different stakeholders (Principals, Directors, HODs, and staff) while offering integrated modules for inventory management, user administration, labs management, and department oversight.

The platform enables educational institutions to digitize their administrative workflows, improve resource allocation, and maintain accurate records of equipment, facilities, and personnel.

## 📋 Features

### User Management
- **Role-based Access Control**: Different access levels for Principal, Director, HOD, Lab Assistants, and Teachers
- **User Profiles**: Personalized profile pages with photo upload capability
- **Authentication**: Secure login system with password encryption
- **Account Management**: Enable/disable user accounts, reset passwords
- **Profile Customization**: Users can update their personal information and profile pictures

### Inventory Management
- **Item Tracking**: Monitor equipment, tools, and resources with detailed information
- **Lab Allocation**: Assign inventory to specific labs within departments
- **Quantity Management**: Track available stock, consumption, and minimum required levels
- **Approval Workflow**: HOD approval for inventory requests from lab staff
- **Search & Filter**: Find items quickly across labs and departments


### Department Administration
- **Department Overview**: View all departments with associated labs and personnel
- **Resource Distribution**: See how resources are allocated across departments
- **Building Management**: Assign buildings to departments for better organization
- **HOD Assignment**: Link departments with their respective Heads
- **Performance Analytics**: Track department efficiency and resource utilization

### Lab Management
- **Lab Creation**: Create and manage laboratory spaces within departments
- **Staff Assignment**: Assign lab assistants and teachers as lab in-charge
- **Inventory Tracking**: Monitor lab-specific equipment and resources
- **Lab Categorization**: Organize labs by purpose, capacity, and department
- **Resource Allocation**: Manage equipment distribution across different labs

### Dashboard Analytics
- **Role-specific Dashboards**: Tailored information display based on user role
- **Top Inventory Items**: Quick view of most stocked items
- **Department Statistics**: Visual representation of department resources
- **Status Monitoring**: Track pending approvals and recent activities
- **Resource Utilization**: Visualize how effectively resources are being used

## Getting Started & Workflow

### Initial Access
1. **Login** with default admin credentials:
   - Username: `admin@gmail.com`
   - Password: `admin`
   - These credentials provide access to all system features for initial setup

### Complete System Setup Process

#### Step 1: User Management
1. Navigate to the **Users** section
2. Add new users with appropriate roles:
   - Principal
   - Director
   - HOD (Head of Department)
   - Teacher
   - Lab Assistant
3. Fill required details including name, email, password, and role assignments
4. Upload profile pictures for users (optional)
5. Set account status to enabled for immediate access

#### Step 2: Department Setup
1. Navigate to the **Departments** section
2. Create departments for your institution (e.g., Computer Science, Electrical Engineering)
3. Assign an HOD to each department (optional - you can leave it blank if no HOD is available)
4. Enter department details including name, code, and description

#### Step 3: Building Management
1. Navigate to the **Buildings** section
2. Create buildings and associate them with specific departments
3. Add building details such as name, location, and number of floors
4. This helps organize the physical infrastructure of your institution

#### Step 4: Lab Creation
1. Navigate to the **Labs** section
2. Create labs for each department with details like lab number, capacity, and purpose
3. Assign personnel to each lab:
   - Lab Assistant (required for lab operation)
   - Teacher as Lab In-charge (oversees lab activities)
4. Link labs to specific buildings and departments for better organization

#### Step 5: Inventory Management
1. Navigate to the **Inventory** section
2. Add inventory items with details:
   - Item name, serial number, quantity
   - Unit cost, purchase date, warranty expiry
   - Assign to specific labs
3. When inventory is added, a request is sent to the respective HOD of the department for approval
4. Track inventory status and availability across the system

### Role-specific Workflows

#### Admin
- Has access to all system features
- Can manage users, departments, labs, and inventory
- System-wide administrative control
- Responsible for initial system setup and maintenance

#### HOD (Head of Department)
1. Login with HOD credentials
2. Access personal dashboard to:
   - View department labs and resources
   - Approve/reject inventory requests
   - Monitor department performance
   - Manage department resources
3. Oversee staff and resource allocation within the department

#### Director
1. Login with Director credentials
2. Access personal dashboard to:
   - View organization-wide statistics
   - Monitor multiple departments
   - Track resource allocation across departments
   - Analyze performance metrics
3. Make strategic decisions based on available data

#### Principal
1. Login with Principal credentials
2. Access personal dashboard to:
   - Get institutional overview
   - Monitor all departments and resources
   - Track overall performance metrics
   - View system-wide statistics

#### Lab Assistant & Teachers
1. Login with respective credentials
2. Manage lab inventory and resources
3. Submit inventory requests for approval
4. Track lab equipment and usage
5. (No specialized dashboard views available for these roles)

## 🏗️ Architecture

The application follows a modern multi-tier architecture:

```
├── Presentation Layer (Thymeleaf, HTML, CSS, JavaScript)
│   ├── Templates (Thymeleaf views)
│   ├── Static Resources (CSS, JS, Images)
│   └── Fragments (Reusable UI components)
│
├── Business Logic Layer (Spring MVC Controllers, Services)
│   ├── Controllers (Handle HTTP requests)
│   ├── Services (Business logic implementation)
│   └── DTOs (Data Transfer Objects)
│
├── Data Access Layer (Spring Data JPA, Repositories)
│   ├── Repositories (Database access interfaces)
│   ├── Entities (Domain models)
│   └── Mappers (Entity-DTO conversions)
│
└── Database Layer (Relational Database)
    ├── Tables (Structured data storage)
    ├── Relationships (Entity associations)
    └── Indexes (Performance optimization)
```

### Data Flow
1. User requests are received by Controllers
2. Business logic is applied through Service classes
3. Data is accessed and manipulated via Repository interfaces
4. Responses are rendered using Thymeleaf templates with Tailwind CSS

## 🛠️ Technology Stack

### Backend
- **Java**: Core programming language
- **Spring Boot**: Application framework
- **Spring MVC**: Web layer
- **Spring Security**: Authentication and authorization
- **Spring Data JPA**: Data access layer
- **Hibernate**: ORM for database operations
- **Maven**: Dependency management

### Frontend
- **Thymeleaf**: Server-side template engine
- **HTML5**: Structure
- **Tailwind CSS**: Styling and responsive design
- **JavaScript/jQuery**: Client-side functionality
- **Font Awesome**: Icons
- **Responsive Design**: Mobile and desktop optimization

### Storage
- **Amazon S3**: For storing user profile photos and other media
- **Relational Database**: For structured application data
- **File System**: For temporary storage and processing

### Tools & Infrastructure
- **Maven**: Dependency management and build
- **Git**: Version control
- **IDE**: Development environment (IntelliJ Idea)

## 📊 Data Models

### Core Entities
- **User**: Faculty and staff profiles with authentication details
  - Personal information, credentials, role assignments
  - Profile photo storage and management
  - Access control attributes

- **Role**: User permissions and access control
  - Role definitions (Admin, Principal, Director, HOD, Teacher, Lab Assistant)
  - Permission mappings
  - Role hierarchies

- **Department**: Academic and administrative divisions
  - Department details and identification
  - HOD assignment
  - Associated buildings and labs

- **Building**: Physical structures associated with departments
  - Building information and location
  - Department associations
  - Capacity and layout details

- **Lab**: Laboratory spaces with assigned personnel
  - Lab specifications and purpose
  - Assigned staff (Lab Assistant, Lab In-charge)
  - Equipment and resources
  - Department and building association

- **InventoryItem**: Equipment and resources tracked by the system
  - Item details and specifications
  - Acquisition information
  - Current status and location
  - Quantity and availability
  - Maintenance records

- **InventoryCategory**: Classification of inventory items
  - Category hierarchies
  - Item grouping and organization

- **InventoryStatus**: Tracking availability and condition of items
  - Status definitions and transitions
  - Approval workflows

## 🔄 Approval Workflows

### Inventory Request and Approval Process
1. Lab Assistant or Teacher adds inventory items to a lab
2. System automatically creates a request to the department HOD
3. HOD receives notification on their dashboard
4. HOD reviews and approves/rejects the inventory request
5. Approved items become available in the inventory system
6. Rejected items require modification and resubmission

### User Account Management
1. Admin creates user accounts with appropriate roles
2. Users receive credentials and can log in to the system
3. Users can update their profiles and change passwords
4. Admin can disable accounts or modify role assignments as needed

## 🔒 Security

The application implements comprehensive security measures:
- **Encrypted Password Storage**: Secure hashing of user passwords
- **CSRF Protection**: Prevention of cross-site request forgery attacks
- **Role-based Access Control**: Granular permissions based on user roles
- **Secure URL Protection**: Endpoint security based on authentication status
- **Session Management**: Secure handling of user sessions
- **Input Validation**: Protection against injection attacks
- **Authentication Workflows**: Secure login and account recovery processes

## 📱 Responsive Design

The application is fully responsive, providing optimal user experience across:
- Desktop computers
- Tablets
- Mobile devices

The UI adapts dynamically to different screen sizes using Tailwind CSS utility classes, ensuring usability on any device.

## 👥 Stakeholders and Roles

- **Admin**:  
  Has full system control and can perform any task within the platform, including user management, settings configuration, and overriding permissions if necessary. This role is primarily responsible for system setup and maintenance.

- **Principal**:  
  Holds system-wide access and administrative oversight. Responsible for high-level decision-making and overall supervision of the institution's resources and departments. Has access to comprehensive dashboards and reports.

- **Director**:  
  Manages resources and activities at the department level. Oversees departmental strategy and coordination between HODs. Can view and analyze resource allocation across multiple departments to ensure optimal distribution.

- **HOD (Head of Department)**:  
  In charge of managing resources specific to their department. Coordinates with staff and ensures departmental requirements are met. Approves inventory requests and manages lab operations within their department.

- **Teacher**:  
  Faculty members who may serve as Lab In-charge. Responsible for overseeing specific labs and their resources. Can initiate inventory requests and manage day-to-day lab operations.

- **Lab Assistant**:  
  Supports lab-related activities such as equipment handling, maintenance, and assisting students or staff during practical sessions. Has access to inventory specific to their assigned labs and can initiate resource requests.
