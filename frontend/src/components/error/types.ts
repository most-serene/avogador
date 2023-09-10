export class ResourceNotFoundError extends Error {
  private _resource: object;
  private _resourceName: string;

  constructor(resource: object, resourceName: string, message: string) {
    super(message);
    this._resource = resource;
    this._resourceName = resourceName;

    if (typeof Error.captureStackTrace === "function") {
      Error.captureStackTrace(this, this.constructor);
    } else {
      this.stack = new Error(message).stack;
    }
  }

  public get resource(): object {
    return this._resource;
  }

  public get resourceName(): string {
    return this._resourceName;
  }
}
