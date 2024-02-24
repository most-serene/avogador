export class ResourceNotFoundError extends Error {
  private readonly _resource: object;
  private readonly _resourceName: string;

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

export class ForbiddenError extends Error {
  private readonly _path: string;

  constructor(path: string, message: string) {
    super(message);
    this._path = path;

    if (typeof Error.captureStackTrace === "function") {
      Error.captureStackTrace(this, this.constructor);
    } else {
      this.stack = new Error(message).stack;
    }
  }

  public get path(): string {
    return this._path;
  }
}

export class ArchivedCourseError extends Error {
  constructor(message: string) {
    super(message);

    if (typeof Error.captureStackTrace === "function") {
      Error.captureStackTrace(this, this.constructor);
    } else {
      this.stack = new Error(message).stack;
    }
  }
}
