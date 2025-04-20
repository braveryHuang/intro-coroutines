package tasks

import contributors.*
import kotlinx.coroutines.*

suspend fun loadContributorsConcurrent(service: GitHubService, req: RequestData): List<User> = coroutineScope {

    /*async starts a new coroutine and returns a Deferred object.
    Deferred represents a concept known by other names such as Future or Promise.
    It stores a computation, but it defers the moment you get the final result;
    it promises the result sometime in the future.

    The total loading time is approximately the same as in the CALLBACKS version,
    but it doesn't need any callbacks.
    What's more, async explicitly emphasizes which parts run concurrently in the code.
    */

    val repos = service
        .getOrgRepos(req.org)
        .also { logRepos(req, it) }
        .body() ?: emptyList()

    val deferredList: List<Deferred<List<User>>> = repos.map { repo ->

        /*
        1. CoroutineDispatcher determines what thread or threads the corresponding coroutine should be run on.
            If you don't specify one as an argument, async will use the dispatcher from the outer scope.

        2. Dispatchers.Default represents a shared pool of threads on the JVM.
            This pool provides a means for parallel execution.
            It consists of as many threads as there are CPU cores available, but it will still have two threads if there's only one core.*/
        //async(Dispatchers.Default) {
        /*
        It's considered good practice to use the dispatcher from the outer scope rather than explicitly specifying it on each end-point.
        If you define loadContributorsConcurrent() without passing Dispatchers.
        Default as an argument, you can call this function in any context:
        with a Default dispatcher, with the main UI thread, or with a custom dispatcher.*/

        //使用外部作用域的调度器被认为是良好的实践，这样在每个端点上就不需要显式指定调度器。
        //意思是这里就不要指定Dispatcher了，继承外部的
        async {
            log("starting loading for ${repo.name}")
            service.getRepoContributors(req.org, repo.name)
                .also { logUsers(repo, it) }
                .bodyList()
        }
    }
    deferredList.awaitAll().flatten().aggregate()
}