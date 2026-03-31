# Pet Shop Inventory Manager

## Описание проекта
`Pet Shop Inventory Manager` — настольное JavaFX-приложение для учёта ассортимента зоомагазина. Приложение позволяет работать с двумя связанными сущностями: категориями товаров и товарами. Одна категория может содержать много товаров, а каждый товар принадлежит одной категории.

Приложение поддерживает:
- CRUD для категорий;
- CRUD для товаров;
- покупку товара с автоматическим уменьшением остатка и генерацией чека;
- сохранение данных между сессиями в собственном текстовом формате;
- сортировку товаров алгоритмом merge sort;
- бинарный поиск товара по `ID`;
- отображение истории действий, которая хранится в собственной структуре данных `Stack`.

## Основные функции
- Добавление, редактирование и удаление категорий.
- Добавление, редактирование и удаление товаров.
- Фильтрация товаров по имени и категории.
- Сортировка товаров по `ID`, названию, цене и количеству.
- Бинарный поиск товара по идентификатору.
- Продажа товара с проверкой остатка.
- Просмотр последних действий пользователя.

## Использованные принципы ООП
- Инкапсуляция: поля доменных классов скрыты и доступны через методы.
- Наследование: `Category` и `Product` наследуются от `NamedEntity`, а `NamedEntity` наследуется от `BaseEntity`.
- Агрегация/композиция: `Receipt` содержит список `ReceiptItem`, `ShopService` агрегирует репозитории и историю действий.
- Декомпозиция: проект разделён на пакеты `model`, `repository`, `persistence`, `service`, `algorithm`, `ui`.

## Хранение данных в файлах
Данные сохраняются в папке `data/`, которая создаётся автоматически при первом запуске:
- `data/categories.db`
- `data/products.db`

Используется собственный текстовый формат:
- поля в записи разделяются символом `|`
- специальные символы экранируются через `\`
- перенос строки хранится как `\n`

Примеры записей:

```text
1|Cats|Dry and wet food for cats
2|Dog Wet Food Chicken|95.0|25|2
```

## UML Use Case Diagram
```mermaid
flowchart LR
    User([User])

    UC1((Create category))
    UC2((Read categories))
    UC3((Update category))
    UC4((Delete category))
    UC5((Create product))
    UC6((Read products))
    UC7((Update product))
    UC8((Delete product))
    UC9((Sort products))
    UC10((Binary search product))
    UC11((Buy product))
    UC12((View receipt))
    UC13((Save and load data))

    User --> UC1
    User --> UC2
    User --> UC3
    User --> UC4
    User --> UC5
    User --> UC6
    User --> UC7
    User --> UC8
    User --> UC9
    User --> UC10
    User --> UC11
    User --> UC12
    User --> UC13
```

## UML Class Diagram
```mermaid
classDiagram
    class BaseEntity {
        -int id
        +getId() int
    }

    class NamedEntity {
        -String name
        +getName() String
        +setName(String) void
    }

    class Category {
        -String description
        +getDescription() String
        +setDescription(String) void
    }

    class Product {
        -double price
        -int quantity
        -int categoryId
        +getPrice() double
        +getQuantity() int
        +getCategoryId() int
        +setPrice(double) void
        +setQuantity(int) void
        +setCategoryId(int) void
    }

    class Receipt {
        -LocalDateTime issuedAt
        -List~ReceiptItem~ items
        +getIssuedAt() LocalDateTime
        +getItems() List~ReceiptItem~
        +getTotalPrice() double
    }

    class ReceiptItem {
        -String productName
        -double unitPrice
        -int quantity
        +getTotal() double
    }

    class ShopService {
        -CategoryRepository categoryRepository
        -ProductRepository productRepository
        -ActionHistoryStack~String~ actionHistory
        +createCategory(String, String) Category
        +updateCategory(int, String, String) void
        +deleteCategory(int) void
        +createProduct(String, double, int, int) Product
        +updateProduct(int, String, double, int, int) void
        +deleteProduct(int) void
        +buyProduct(int, int) Receipt
        +findProductByIdBinarySearch(int) Product
    }

    class CategoryRepository
    class ProductRepository
    class CategoryFileStore
    class ProductFileStore
    class MergeSort
    class BinarySearch
    class ActionHistoryStack~T~

    BaseEntity <|-- NamedEntity
    BaseEntity <|-- Receipt
    NamedEntity <|-- Category
    NamedEntity <|-- Product
    Category "1" --> "many" Product : groups
    Receipt "1" *-- "many" ReceiptItem
    ShopService --> CategoryRepository
    ShopService --> ProductRepository
    ShopService --> ActionHistoryStack~String~
    CategoryRepository --> CategoryFileStore
    ProductRepository --> ProductFileStore
    ShopService ..> MergeSort
    ShopService ..> BinarySearch
```

## Как критерии закрываются в проекте
- Две связанные сущности: `Category` и `Product` c отношением one-to-many.
- Полный CRUD: реализован для обеих сущностей в интерфейсе и сервисном слое.
- File I/O: чтение и запись выполняются студентом через `CategoryFileStore` и `ProductFileStore`.
- Custom data structure: `ActionHistoryStack<T>`.
- Sort: собственная реализация `MergeSort`.
- Search: собственная реализация `BinarySearch`.
