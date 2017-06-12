package ai.susi.server.api.cms;

import ai.susi.DAO;
import ai.susi.json.JsonObjectWithDefault;
import ai.susi.server.*;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.storage.file.FileRepositoryBuilder;
import org.json.JSONArray;
import org.json.JSONObject;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created by chetankaushik on 12/06/17.
 */
public class GetCommitHistory extends AbstractAPIHandler implements APIHandler {
    JSONObject commit = new JSONObject();
    JSONArray commitsArray =  new JSONArray();

    private static final long serialVersionUID = 4538304346942632187L;

    @Override
    public String getAPIPath() {
        return "/aaa/getCommitHistory.json";
    }

    @Override
    public BaseUserRole getMinimalBaseUserRole() {
        return BaseUserRole.ANONYMOUS;
    }

    @Override
    public JSONObject getDefaultPermissions(BaseUserRole baseUserRole) {
        return null;
    }

    @Override
    public ServiceResponse serviceImpl(Query call, HttpServletResponse response, Authorization rights, final JsonObjectWithDefault permissions) throws APIException {


        String path = DAO.susi_skill_repo.toString();
        //Add to git
        FileRepositoryBuilder builder = new FileRepositoryBuilder();
        Repository repository = null;
        try {
            repository = builder.setGitDir((DAO.susi_skill_repo))
                    .readEnvironment() // scan environment GIT_* variables
                    .findGitDir() // scan up the file system tree
                    .build();

            try (Git git = new Git(repository)) {

                Iterable<RevCommit> logs;

                logs = git.log()
                        .call();
                int i = 0;
                for (RevCommit rev : logs) {
                    commit = new JSONObject();
                    commit.put("commitRev", rev);
                    commit.put("commitName", rev.getName());
                    commit.put("commitID", rev.getId().getName());
                    commit.put("commit_message", rev.getShortMessage());

                    commitsArray.put(i, commit);
                    i++;
                }

            } catch (GitAPIException e) {
                e.printStackTrace();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }



        return new ServiceResponse(commitsArray);
    }

}
