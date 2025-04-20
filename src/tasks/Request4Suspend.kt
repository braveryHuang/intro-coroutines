package tasks

import contributors.*

suspend fun loadContributorsSuspend(service: GitHubService, req: RequestData): List<User> {
    val repos = service
        .getOrgRepos(req.org)
        .also { logRepos(req, it) }
        .body() ?: emptyList()

    return repos.flatMap { repo ->
        service
            .getRepoContributors(req.org, repo.name)
            .also { logUsers(repo, it) }
            .bodyList()
    }.aggregate()
}

/*Coroutines run on top of threads and can be suspended. When a coroutine is suspended,
the corresponding computation is paused, removed from the thread, and stored in memory.
Meanwhile, the thread is free to be occupied by other tasks:

When the computation is ready to be continued,
it is returned to a thread (not necessarily the same one).

Suspending functions treat the thread fairly and don't block it for "waiting".
However, this doesn't yet bring any concurrency into the picture.
*/

