# Littlepay Coding Challenge - Architectural Improvements

This document outlines architectural improvements that could be applied to the codebase to make it more scalable, maintainable, and robust.

## Current Architecture

The current application follows a simple service-oriented architecture with the following components:

1. **Data Models (DTOs)**: Represent the core data structures
2. **Service Layer**: Contains the business logic
3. **CSV Processing**: Handles reading input data and writing output data

While this architecture works for the current requirements, there are several improvements that could be made to enhance the application's scalability and maintainability.

## Proposed Architecture

### 1. Layered Architecture

Implement a clear layered architecture with the following layers:

```
┌─────────────────────────────────┐
│           Presentation          │
│  (REST API, CLI, or File I/O)   │
└───────────────┬─────────────────┘
                │
┌───────────────▼─────────────────┐
│         Service Layer           │
│    (Business Logic, Validation) │
└───────────────┬─────────────────┘
                │
┌───────────────▼─────────────────┐
│       Repository Layer          │
│     (Data Access, Storage)      │
└───────────────┬─────────────────┘
                │
┌───────────────▼─────────────────┐
│         Domain Model            │
│  (Entities, Value Objects)      │
└─────────────────────────────────┘
```

#### Benefits:
- Clear separation of concerns
- Improved testability
- Better maintainability
- Easier to extend with new features

### 2. Domain-Driven Design (DDD)

Apply DDD principles to better model the domain:

1. **Entities**: Objects with identity (e.g., Trip, Tap)
2. **Value Objects**: Immutable objects without identity (e.g., TravelPrice)
3. **Aggregates**: Clusters of entities and value objects (e.g., Trip with associated Taps)
4. **Repositories**: Interfaces for data access
5. **Services**: Domain logic that doesn't fit into entities or value objects

#### Example Domain Model:

```
┌─────────────────┐     ┌─────────────────┐
│      Trip       │     │       Tap       │
├─────────────────┤     ├─────────────────┤
│ - id            │1   2│ - id            │
│ - startTap      ├─────┤ - dateTime      │
│ - endTap        │     │ - type          │
│ - status        │     │ - stopId        │
│ - cost          │     │ - companyId     │
│ - duration      │     │ - busId         │
└─────────────────┘     │ - pan           │
                        └─────────────────┘
```

### 3. Hexagonal Architecture (Ports and Adapters)

Implement a hexagonal architecture to decouple the core domain from external concerns:

```
┌─────────────────────────────────────────────────┐
│                                                 │
│  ┌─────────────────────────────────────────┐    │
│  │                                         │    │
│  │             Core Domain                 │    │
│  │                                         │    │
│  └───────────┬─────────────┬───────────────┘    │
│              │             │                    │
│  ┌───────────▼───┐   ┌─────▼───────────┐        │
│  │               │   │                 │        │
│  │  Input Ports  │   │  Output Ports   │        │
│  │               │   │                 │        │
│  └───────┬───────┘   └────────┬────────┘        │
│          │                    │                 │
└──────────┼────────────────────┼─────────────────┘
           │                    │
┌──────────▼───────┐   ┌────────▼────────┐
│                  │   │                 │
│  Input Adapters  │   │ Output Adapters │
│  (REST, CLI)     │   │ (CSV, DB)       │
│                  │   │                 │
└──────────────────┘   └─────────────────┘
```

#### Benefits:
- Decouples the core domain from external concerns
- Makes it easier to replace external components
- Improves testability
- Enhances maintainability

### 4. Event-Driven Architecture

For a more scalable solution, consider implementing an event-driven architecture:

```
┌─────────────┐    ┌─────────────┐    ┌─────────────┐
│             │    │             │    │             │
│  Tap Event  │───►│  Processor  │───►│ Trip Event  │
│  Producer   │    │             │    │  Consumer   │
│             │    │             │    │             │
└─────────────┘    └─────────────┘    └─────────────┘
                          │
                          ▼
                   ┌─────────────┐
                   │             │
                   │  Data Store │
                   │             │
                   └─────────────┘
```

#### Benefits:
- Improved scalability
- Better fault tolerance
- Easier to distribute processing
- Supports real-time processing

## Implementation Recommendations

### 1. Service Layer

Define clear interfaces for services:

```
public interface TripService {
    Trip processTrip(Tap startTap, Tap endTap);
    List<Trip> processAllTaps(List<Tap> taps);
}

public interface PricingService {
    BigDecimal calculatePrice(StopId from, StopId to);
    BigDecimal getMaxPriceForStop(StopId stopId);
}
```

### 2. Repository Layer

Implement repositories for data access:

```
public interface TripRepository {
    void save(Trip trip);
    List<Trip> findAll();
    List<Trip> findByPan(String pan);
}

public interface PriceRepository {
    BigDecimal findPrice(StopId from, StopId to);
    List<TravelPrice> findAll();
}
```

### 3. Domain Model

Create rich domain models with business logic:

```
public class Trip {
    private final Tap startTap;
    private final Tap endTap;
    private final TripStatus status;
    private final BigDecimal cost;
    
    public Trip(Tap startTap, Tap endTap) {
        this.startTap = startTap;
        this.endTap = endTap;
        this.status = calculateStatus();
        this.cost = calculateCost();
    }
    
    private TripStatus calculateStatus() {
        if (endTap == null) {
            return TripStatus.INCOMPLETE;
        }
        if (startTap.getStopId().equals(endTap.getStopId())) {
            return TripStatus.CANCELLED;
        }
        return TripStatus.COMPLETED;
    }
    
    private BigDecimal calculateCost() {
        // Cost calculation logic
    }
    
    public long getDuration() {
        if (endTap == null) {
            return 0;
        }
        return endTap.getDateTime().toEpochSecond() - 
               startTap.getDateTime().toEpochSecond();
    }
}
```

## Migration Strategy

To migrate from the current architecture to the proposed one:

1. **Incremental Approach**: Implement changes incrementally, starting with the most critical components
2. **Parallel Implementation**: Develop new components alongside existing ones
3. **Feature Toggles**: Use feature toggles to gradually switch to new implementations
4. **Comprehensive Testing**: Ensure thorough testing at each step

## Conclusion

The proposed architectural improvements would significantly enhance the application's scalability, maintainability, and robustness. By implementing a clear layered architecture, applying DDD principles, and potentially adopting a hexagonal or event-driven architecture, the application would be better positioned to handle future requirements and scale effectively.