package collect.debug;

import com.common.collect.test.debug.git.GitUtil;

import java.util.List;

/**
 * Created by hznijianfeng on 2020/4/29.
 */

public class GitUtilTest {

    public static void main(String[] args) {
//        GitUtil.remoteRemoteBranch("D:\\projects\\sf-collect\\collect-common", "origin/dependabot/maven/collect-common/com.fasterxml.jackson.core-jackson-databind-2.9.10.4");
        String baseDir = "";
//        List<String> localBranch = GitUtil.getLocalBranches(baseDir);
//        System.out.println("本地分支:" + localBranch.size() + "\t" + localBranch);
        List<String> remoteBranches = GitUtil.getRemoteBranches(baseDir);
        System.out.println("远程分支:" + remoteBranches.size() + "\t" + remoteBranches);

        int count = 0;
        // 根据代码是否已在 master 捞分支
        for (String remoteBranch : remoteBranches) {
            if (remoteBranch.contains("origin/master")) {
                continue;
            }
            if (GitUtil.hasMergeB2A(baseDir, "origin/master", remoteBranch)) {
                count++;
                System.out.println(remoteBranch);
            }
        }
        System.out.println("当前分支代码已都在master的分支数量：" + count);


        // 根据某个时间点捞分支
//        Date limitTime = DateUtil.parseDate("2020-01-01 00:00:00", "yyyy-MM-dd HH:mm:ss");
//        for (String remoteBranch : remoteBranches) {
//            String log = GitUtil.logLastCommitTime(baseDir, remoteBranch);
//            if (EmptyUtil.isEmpty(log)) {
//                continue;
//            }
//            Date commitTime = DateUtil.parseDate(log.substring(0, 19), "yyyy-MM-dd HH:mm:ss");
//            if (commitTime.before(limitTime)) {
//                count++;
//                System.out.println(log + "\t" + remoteBranch);
////                GitUtil.remoteRemoteBranch(baseDir, remoteBranch);
//            }
//        }
//        System.out.println(DateUtil.format(limitTime, "yyyy-MM-dd HH:mm:ss") + " 之前的分支数量：" + count);
    }

}
