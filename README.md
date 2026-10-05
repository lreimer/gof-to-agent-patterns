# From GoF to AI Agents 

Demo repository for Design Patterns for the Agentic Era.

## Design Patterns

**Adapter** is the primary GoF pattern for `FunctionTool`, as it adapts an ordinary Java method making it usable as agent tool. It builds the function declaration, converts tool arguments into method parameters, invokes the method through reflection, and normalizes its result. It also has a **Command-like** aspect because it encapsulates an executable operation.

**Chain of Responsibility** is the closest GoF match for ADK’s model and tool callback mechanism for interception and control; Observer-like for logging-only usage.

Each callback can either:
- Return Maybe.empty() to let processing continue.
- Return a value to override the model or tool result, potentially short-circuiting normal execution.

The logging callback is **Observer-like**, because it reacts to a lifecycle event without changing behavior. However, callbacks generally are not pure Observers: they can modify or stop processing.

## References

- 

## Maintainer

M.-Leander Reimer (@lreimer), <mario-leander.reimer@qaware.de>

## License

This software is provided under the MIT open source license, read the `LICENSE` file for details.
